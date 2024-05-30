(ns ^:no-doc polylith.clj.core.deps.project-brick-deps.project-deps
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.project-brick-deps.from-namespaces :as from-namespaces]
            [polylith.clj.core.deps.project-brick-deps.shared :as shared]))

(defn add-child-paths [{:keys [paths]} child-brick-id child-paths]
  (let [child-paths (if (empty? child-paths)
                      [[child-brick-id]]
                      (mapv #(vec (cons child-brick-id %)) child-paths))]
    (concat paths child-paths)))

(defn transfer-dep [brick-id child-brick-id brick-id->deps]
  (let [deps (brick-id->deps brick-id)
        {:keys [direct paths indirect]} (brick-id->deps child-brick-id)]
    (assoc deps :indirect (set (concat (:indirect deps) direct indirect))
                :paths (add-child-paths deps child-brick-id paths)
                :completed? true)))

(defn update-dep [brick-id child-brick-id brick-id->deps]
  (let [deps (brick-id->deps brick-id)
        {:keys [direct paths indirect]} (brick-id->deps child-brick-id)
        new-indirect (set (concat (:indirect deps) direct indirect))]
    (assoc deps :indirect new-indirect
                :paths (add-child-paths deps child-brick-id paths)
                :completed? true)))

(defn update-deps!
  "This is the core recursive function that calculates all dependencies for a project,
   based on the dependencies in brick-id->brick-ids. The result is continuously updated
   in the brick-id->deps atom.

   If the :completed? flag is set for the current brick-id, then we transfer indirect
   dependencies and paths to the parent-brick-id, instead of recursively calling
   all its dependencies, as a way to improve the performance.

   After we have recursively called the brick's dependencies, we update paths and indirect
   dependencies for the parent brick-id."
  [parent-brick-id brick-id brick-id->brick-ids brick-id->deps visited]
  (if (get-in @brick-id->deps [brick-id :completed?])
    (swap! brick-id->deps assoc parent-brick-id
           (transfer-dep parent-brick-id brick-id @brick-id->deps))
    (do
      (when (not (contains? visited brick-id))
        (let [brick-ids (brick-id->brick-ids brick-id)]
          (if (empty? brick-ids)
            (swap! brick-id->deps assoc brick-id
                   (assoc (@brick-id->deps brick-id) :completed? true))
            (doseq [depends-on-brick-id brick-ids]
              (update-deps! brick-id depends-on-brick-id brick-id->brick-ids brick-id->deps
                            (conj visited brick-id))))))
      (swap! brick-id->deps assoc parent-brick-id
             (update-dep parent-brick-id brick-id @brick-id->deps)))))

(defn brick-names
  "Converts from interface and base names to brick names."
  [deps ifc->comp-name]
  (map #(ifc->comp-name % %) deps))

(defn brick-ids-in-project [component-names base-names bricks]
  (set (concat (map :interface-name
                    (filter #(contains? (set component-names) (:name %))
                            bricks))
               (map :name
                    (filter #(contains? (set base-names) (:name %))
                            bricks)))))

(defn drop-suffix [brick-id]
  (first (str/split brick-id #" ")))

(defn drop-suffixes
  "Drops the (t) suffix (if any). We don't keep track of whether we depend on the src or test context,
   only if we depend from src or test context."
  [brick-ids]
  (set (map drop-suffix brick-ids)))

(defn circular-path
  "The circular reference doesn't need to start at the first element
   in the path, and that's why we need to drop non brick-id elements
   from the end."
  [brick-id path]
  (let [cleaned-path (reverse (drop-while #(not= brick-id %)
                                          (reverse path)))]
    (when (> (count cleaned-path) 1)
      (conj cleaned-path brick-id))))

(defn circular-dep [brick-id indirect paths]
  (when (contains? indirect brick-id)
    (first (filter identity
                   (map #(circular-path brick-id %)
                        paths)))))

(defn missing-deps
  "All bricks that we depend on that is not included in the project are considered missing,
   with the exception if brick-ids-to-check is not nil, we only check if any of these are missing."
  [brick-ids brick-ids-in-project brick-ids-to-check]
  (set/difference (if brick-ids-to-check
                    (set/intersection brick-ids brick-ids-to-check)
                    brick-ids)
                  brick-ids-in-project))

(defn finalize-deps
  "At this moment, all the dependencies for a specific project has been calculated and are stored
   in brick-id->deps, with a key for each brick-id. All the direct and indirect dependencies are
   directly accessible from that map via the incoming brick-id.

   Then we calculate missing dependencies on interfaces and bases, which are all dependencies that
   are not also included in the project.

   Finally, we calculate circular dependencies based on the indirect dependencies (if we have any
   circular dependencies, then the brick-id will also be found among the indirect dependencies).

   During those calculations, we also make sure we translate interfaces names to component names
   (used by the project)."
  [brick-id brick-id->deps ifc->comp-name all-brick-ids brick-ids-in-project brick-ids-to-check]
  (let [{:keys [direct indirect paths]} (brick-id->deps brick-id)
        circular (circular-dep brick-id indirect paths)
        all-direct (set/intersection (drop-suffixes direct) all-brick-ids)
        all-indirect (set/intersection (drop-suffixes indirect) all-brick-ids)
        direct (set/intersection all-direct brick-ids-in-project)
        indirect (set/difference (set/intersection all-indirect brick-ids-in-project) direct)
        missing-direct (missing-deps all-direct brick-ids-in-project brick-ids-to-check)
        missing-indirect (missing-deps indirect all-indirect brick-ids-to-check)
        has-missing? (or (seq missing-direct) (seq missing-indirect))]
    (cond-> {}
            (seq direct) (assoc :direct (vec (sort (brick-names direct ifc->comp-name))))
            (seq indirect) (assoc :indirect (vec (sort (brick-names indirect ifc->comp-name))))
            has-missing? (assoc :missing-ifc-and-bases {:direct (-> missing-direct sort vec)
                                                        :indirect (-> missing-indirect sort vec)})
            (seq circular) (assoc :circular (vec (brick-names circular ifc->comp-name))))))

(defn merge-missing [{src1 :src test1 :test}
                     {src2 :src test2 :test}]
  (let [src (vec (set (concat src1 src2)))
        test (vec (set (concat test1 test2)))]
    [{:src src
      :test test}
     (or (seq src)
         (seq test))]))

(defn merge-deps
  "If a brick is only used from the test context, then all its dependencies (src + test)
   are treated as test dependencies, so we merge them."
  [{direct1 :direct indirect1 :indirect circular1 :circular missing-ifc-and-bases1 :missing-ifc-and-bases}
   {direct2 :direct indirect2 :indirect circular2 :circular missing-ifc-and-bases2 :missing-ifc-and-bases}]
  (let [direct (vec (sort (set (concat direct1 direct2))))
        indirect (vec (sort (set/difference (set (concat indirect1 indirect2)) direct)))
        circular (if (seq circular1) circular1 circular2)
        [missing missing?] (merge-missing missing-ifc-and-bases1 missing-ifc-and-bases2)]
    (cond-> {}
            (seq direct) (assoc :direct direct)
            (seq indirect) (assoc :indirect indirect)
            missing? (assoc :missing-ifc-and-bases missing)
            (seq circular) (assoc :circular circular))))

(defn brick-deps
  "When this function is called, we have already calculated all dependencies for the project, and passes
   it in as brick-id->deps. We know that all dependencies are stored using both src (e.g. 'util') and test
   (e.g. 'util (t)') as keys. We use this to let the finalize-deps function pick out the dependencies for
   both the src and the test context, and make all the calculations for us.

   If a brick is only included in the test context for the project, then we treat all dependencies as test dependencies."
  [{:keys [brick-id]} brick-id->deps ifc->comp-name all-brick-ids brick-ids-in-project-src brick-ids-in-project-test test-only-brick-ids brick-ids-to-check]
  (let [test-brick-id (str brick-id " (t)")
        src-deps (finalize-deps brick-id brick-id->deps ifc->comp-name all-brick-ids brick-ids-in-project-src nil)
        test-deps (finalize-deps test-brick-id brick-id->deps ifc->comp-name all-brick-ids brick-ids-in-project-test brick-ids-to-check)]
    (if (contains? test-only-brick-ids brick-id)
      {:src  {}
       :test (merge-deps src-deps test-deps)}
      {:src  src-deps
       :test test-deps})))

(defn initial-dep
  "We know the direct dependencies from start, which will later be used to calculate indirect dependencies.
   The paths will later be used when giving an example of a circular dependency (if any)."
  [brick-id brick-id->brick-ids]
  [brick-id
   {:direct (brick-id->brick-ids brick-id)
    :indirect #{}
    :paths []}])

(defn brick-names-to-ids
  "Converts component names to interface names so that they can be used in the dependency calculations.
   Bases will keep their names."
  [bricks-to-test components]
  (when bricks-to-test
    (let [name->brick-id (into {} (map (juxt :name :interface-name)
                                       components))]
      (set (map #(name->brick-id % %) bricks-to-test)))))

(defn ->workspace [{:keys [alias settings]}]
  {:alias alias
   :suffixed-top-ns (-> settings :top-namespace common/suffix-ns-with-dot)})

(defn calculate-project-deps
  [project-name
   components1 bases1 workspaces
   component-names-src1 component-names-test1
   base-names-src1 base-names-test1
   suffixed-top-ns brick-names-to-test]
  "Calculates the src and test dependencies for a project. The returned dependencies
   are stored in a map with a :src and :test key and includes a key for each brick that is included
   in the project, together with the direct, indirect, and circular dependencies (if any) +
   missing dependencies on interfaces and bases (if any).

   A project can only have one component per interface (one implementation) and we take advantage of this by
   translating all component names to their interface names when calculating the dependencies for a project.
   The bases are already unique (a base is not allowed to have the same name as an interface and vice versa)
   so we mix interface and base names and call them brick-ids, when calculating the brick-id->deps atom/map.

   We only keep track on dependencies at the brick level, not the namespace level. This means we are stricter
   than the compiler, and that we consider dependencies like A > B > A or A > B > C > A as circular dependencies,
   even in those cases where the code compiles.

   The src context of a brick can only depend on other brick's src context, never their test context (because it's
   not available). A brick's test context often only depends on other component's src context, but it's also allowed
   to depend on their test context.

   To get the dependency calculations right, we split the keys in brick-id->brick-ids into src and test keys,
   so that e.g. 'util' will get the name 'util' for the src context and 'util (t)' for the test context.

   A project can have a different set of components for src and test (e.g. a test-helper only used in test)
   and their dependency graph may also differ. This is why it's important to have keys for both the src and test
   context. If the test context depends on its own src context, then it will also include the dependencies
   from the src context (we never store dependencies on ourselves in the dependency graph).

   If a brick is only included in the test context of a project, then all its dependencies will be treated as test
   dependencies (the src dependencies will be merged into the test dependencies and then set as empty).

   In workspace.edn under the :projects key, it's possible to specify which bricks to include or exclude for a
   project. This can be useful if we e.g. only want to run tests for one of our projects (it's passed in as
   bricks-to-test). In this example we can specify :test to [] or {:include []} for all our projects except for one.

   When all dependencies are calculated, we need to pass in bricks-to-test to brick-deps to make sure we don't
   treat bricks that are excluded from testing, as missing.

   One more thing to remember. Bricks are normally included in a project using the :local/root syntax,
   and in that case we will inherit the brick's :src and :test context from each brick. The :paths and
   :aliases > :test > :extra-paths syntax is only needed for the development project if your IDE doesn't support
   the :local/root syntax.
   The recommendation is to use the :local/root syntax in all your projects if it's supported by your IDE."
  (let [brick-names (set (concat component-names-src1 component-names-test1 base-names-src1 base-names-test1))
        all-bricks (concat components1 bases1)
        bricks (filter #(contains? brick-names (:name %))
                       all-bricks)
        component-names (set (concat component-names-src1 component-names-test1))
        ifc->comp-name (into {} (map (juxt :interface-name :name)
                                     (concat components1
                                             (filter #(contains? component-names (:name %))
                                                     components1))))
        brick-ids-to-check (brick-names-to-ids brick-names-to-test components1)
        all-brick-ids (set (map :brick-id all-bricks))
        brick-ids-in-project-src (brick-ids-in-project component-names-src1 base-names-src1 bricks)
        brick-ids-in-project-test (brick-ids-in-project (concat component-names-src1 component-names-test1) (concat base-names-src1 base-names-test1) bricks)
        test-only-brick-ids (set/difference brick-ids-in-project-test brick-ids-in-project-src)
        wss (concat [{:suffixed-top-ns suffixed-top-ns}]
                    (map ->workspace workspaces))
        src-brick-id->brick-ids (into {} (map #(from-namespaces/extract-src-deps % wss)
                                              bricks))
        all-test-namespaces (set (mapcat #(shared/all-test-namespaces bricks %)
                                         wss))
        all-test-brick-id->brick-ids (into {} (map #(from-namespaces/extract-test-deps % wss all-test-namespaces src-brick-id->brick-ids)
                                                   bricks))
        ;; Here we store all our dependencies in a map. Each key is a brick-id (a component interface name or base name) and
        ;; each value is a vector of the brick-ids it depends on. All brick-ids, in both keys and values, is prefixed with (t)
        ;; (e.g. "util (t)") if it belongs to the test context.
        brick-id->brick-ids (merge-with into src-brick-id->brick-ids all-test-brick-id->brick-ids)
        ;; Prepare all dependencies with an initial state where :direct dependencies are set.
        brick-id->deps (atom (into {} (map #(initial-dep % brick-id->brick-ids)
                                           (keys brick-id->brick-ids))))]
    ;; Step 1: Update brick-id->deps with all our dependencies for this particular project,
    ;;         which calculates :indirect and :paths (used if we have circular dependencies).
    (doseq [brick bricks]
      (let [src-brick-id (:brick-id brick)
            test-brick-id (str src-brick-id " (t)")]
        (doseq [brick-id (brick-id->brick-ids src-brick-id)]
          (update-deps! src-brick-id brick-id brick-id->brick-ids brick-id->deps #{src-brick-id}))
        (doseq [brick-id (brick-id->brick-ids test-brick-id)]
          (update-deps! test-brick-id brick-id brick-id->brick-ids brick-id->deps #{test-brick-id}))))
    ;; Step 2: For each brick, convert interface names to component names + calculate missing and circular dependencies.
    (into {} (map (juxt :name #(brick-deps % @brick-id->deps ifc->comp-name all-brick-ids brick-ids-in-project-src brick-ids-in-project-test test-only-brick-ids brick-ids-to-check))
                  bricks))))

(defn full-name [alias name]
  (str alias "/" name))

(defn ->component [{:keys [name interface interface-deps namespaces]}]
  {:name name
   :interface-name (:name interface)
   :brick-id (:name interface)
   :deps interface-deps
   :namespaces namespaces})

(defn ->base-deps [interface-deps base-deps]
  (let [src-deps (vec (concat (:src interface-deps) (:src base-deps)))
        test-deps (vec (concat (:test interface-deps) (:test base-deps)))]
    {:src src-deps
     :test test-deps}))

(defn ->base [{:keys [name interface-deps base-deps namespaces]}]
  {:name name
   :brick-id name
   :deps (->base-deps interface-deps base-deps)
   :namespaces namespaces})

(defn ws-component [alias {:keys [name interface interface-deps namespaces]}]
  (let [component-name (full-name alias name)
        interface-name (full-name alias (:name interface))]
    {:name component-name
     :alias alias
     :interface-name interface-name
     :brick-id interface-name
     :deps interface-deps
     :namespaces namespaces}))

(defn ws-base [alias {:keys [name interface-deps base-deps namespaces]}]
  (let [base-name (full-name alias name)]
    {:name base-name
     :alias alias
     :brick-id base-name
     :deps (->base-deps interface-deps base-deps)
     :namespaces namespaces}))

(defn ws-components [{:keys [alias components]}]
  (mapv #(ws-component alias %) components))

(defn ws-bases [{:keys [alias bases]}]
  (mapv #(ws-base alias %) bases))

(defn project-deps
  [project-name
   components bases workspaces
   component-names-src component-names-test
   base-names-src base-names-test
   component-names-src-x component-names-test-x
   base-names-src-x base-names-test-x
   suffixed-top-ns brick-names-to-test]
  (let [bases1 (into [] cat [(mapv ->base bases)
                             (mapcat ws-bases workspaces)])
        components1 (into [] cat [(mapv ->component components)
                                  (mapcat ws-components workspaces)])
        component-names-src1 (concat component-names-src component-names-src-x)
        component-names-test1 (concat component-names-test component-names-test-x)
        base-names-src1 (concat base-names-src base-names-src-x)
        base-names-test1 (concat base-names-test base-names-test-x)]
    (calculate-project-deps project-name
                            components1 bases1 workspaces
                            component-names-src1 component-names-test1
                            base-names-src1 base-names-test1
                            suffixed-top-ns brick-names-to-test)))
