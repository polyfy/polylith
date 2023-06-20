(ns polylith.clj.core.deps.project-brick-deps
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]))

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

(defn test-namespace [{:keys [namespace]} suffixed-top-ns]
  (let [{:keys [root-ns depends-on-ns]} (common/extract-namespace suffixed-top-ns namespace)]
    (when root-ns
      [(str root-ns "." depends-on-ns)])))

(defn test-namespaces [{:keys [namespaces]} suffixed-top-ns]
  (mapcat #(test-namespace % suffixed-top-ns) (:test namespaces)))

(defn all-test-namespaces
  "Extracts all test namespaces from alla bricks in the project."
  [bricks suffixed-top-ns]
  (set (mapcat #(test-namespaces % suffixed-top-ns) bricks)))

(defn ->brick-id [{:keys [name interface]}]
  (or (:name interface) name))

(defn src-deps-from-namespaces
  "Iterates through all namespaces for a brick's src context and calculates its dependencies."
  [{:keys [namespaces] :as brick} suffixed-top-ns]
  (let [brick-id (->brick-id brick)]
    [brick-id
     (vec (sort (set (filter #(and (not= brick-id %)
                                   (-> % nil? not))
                             (map #(:root-ns (common/extract-namespace suffixed-top-ns %))
                                  (mapcat :imports (:src namespaces)))))))]))

(defn test-deps-from-namespace
  "Calculates the test dependencies for a brick's namespace. If it's a test namespace that depends
   on a namespace of its own, then it inherits all the dependencies from the src context."
  [suffixed-top-ns ns-to-extract brick-id all-test-namespaces src-brick-id->brick-ids]
  (let [{:keys [root-ns depends-on-ns]} (common/extract-namespace suffixed-top-ns ns-to-extract)]
    (if (contains? all-test-namespaces (str root-ns "." depends-on-ns))
      [(str root-ns " (t)")]
      (if (= brick-id root-ns)
        ;; if the test context depends on the src context of itself, use the dependencies from the src context.
        (src-brick-id->brick-ids brick-id)
        [root-ns]))))

(defn test-deps-from-namespaces
  "Iterates through all namespaces for a brick's test context and calculates its dependencies."
  [{:keys [namespaces] :as brick} suffixed-top-ns all-test-namespaces src-brick-id->brick-ids]
  (let [plain-brick-id (->brick-id brick)
        brick-id (str plain-brick-id " (t)")]
    [brick-id
     (vec (sort (set (filter #(and (not= brick-id %)
                                   (-> % nil? not))
                             (mapcat #(test-deps-from-namespace suffixed-top-ns % plain-brick-id all-test-namespaces src-brick-id->brick-ids)
                                     (mapcat :imports (:test namespaces)))))))]))

(defn brick-names
  "Converts from interface and base names to brick names."
  [deps ifc->comp]
  (map #(ifc->comp % %) deps))

(defn brick-ids-in-project [component-names base-names bricks]
  (set (concat (map #(-> % :interface :name)
                    (filter #(contains? (set component-names) (:name %))
                            bricks))
               (map :name
                    (filter #(contains? (set base-names) (:name %))
                            bricks)))))

(defn include-test?
  "Checks if the brick is included in workspace.edn > :projects > PROJECT-KEY > :test > :include.
   If the :test key is not present, then the brick is treated as included."
  [{:keys [name]} bricks-to-test]
  (or (nil? bricks-to-test)
      (contains? bricks-to-test name)))

(defn drop-suffix [brick-id]
  (first (str/split brick-id #" ")))

(defn drop-suffixes
  "Drops the (t) suffix (if any). We don't keep track of whether we depend on the src or test context,
   only if we depend from src or test context."
  [brick-ids]
  (set (map drop-suffix brick-ids)))

(defn circular-dep [brick-id indirect paths]
  (when (contains? indirect brick-id)
    (cons brick-id
          (first (filterv #(= brick-id (last %))
                          paths)))))

(defn missing-deps
  "All bricks that we depend on that is not included in the project are considered missing,
   with the exception if brick-ids-to-check is not nil, we only check if any of these are missing."
  [brick-ids all-brick-ids-in-project brick-ids-to-check]
  (set/difference (if brick-ids-to-check
                    (set/intersection brick-ids brick-ids-to-check)
                    brick-ids)
                  all-brick-ids-in-project))

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
  [brick-id brick-id->deps ifc->comp all-brick-ids brick-ids-in-project brick-ids-to-check]
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
            (seq direct) (assoc :direct (vec (sort (brick-names direct ifc->comp))))
            (seq indirect) (assoc :indirect (vec (sort (brick-names indirect ifc->comp))))
            has-missing? (assoc :missing-ifc-and-bases {:direct (-> missing-direct sort vec)
                                                        :indirect (-> missing-indirect sort vec)})
            (seq circular) (assoc :circular (vec (brick-names circular ifc->comp))))))

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
        indirect (vec (sort (set (concat indirect1 indirect2))))
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
  [brick brick-id->deps ifc->comp all-brick-ids brick-ids-in-project brick-ids-in-project-test test-only-brick-ids brick-ids-to-test]
  (let [src-brick-id (->brick-id brick)
        test-brick-id (str src-brick-id " (t)")
        src-deps (finalize-deps src-brick-id brick-id->deps ifc->comp all-brick-ids brick-ids-in-project nil)
        test-deps (finalize-deps test-brick-id brick-id->deps ifc->comp all-brick-ids brick-ids-in-project-test brick-ids-to-test)]
    (if (contains? test-only-brick-ids src-brick-id)
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
    (let [name->brick-id (map (fn [{:keys [name interface]}] [name (:name interface)])
                              components)]
      (set (map #(name->brick-id % %) bricks-to-test)))))

(defn project-deps
  [components bases component-names-src component-names-test base-names-src base-names-test suffixed-top-ns bricks-to-test]
  "Calculate the src and test dependencies for a project. The returned dependencies
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

   When all dependencies are calculated, we need to pass in bricks-to-test to brick-deps so make sure we don't
   treat bricks that are excluded from testing, as missing.

   One more thing to remember. Bricks are normally included in a project using the :local/root syntax,
   and in that case we will inherit the brick's :src and :test context from each brick. The :paths and
   :aliases > :test > :extra-paths syntax is only needed for the development project if your IDE doesn't support
   the :local/root syntax.
   The recommendation is to use the :local/root syntax in all your projects if it's supported by your IDE."
  (let [brick-names (set (concat component-names-src component-names-test base-names-src base-names-test))
        bricks (filter #(contains? brick-names (:name %))
                       (concat bases components))
        component-names (set (concat component-names-src component-names-test))
        ;; Make sure we pick the right component if more than one for an interface.
        ifc->comp (into {} (map (juxt #(-> % :interface :name) :name)
                                (concat components
                                        (filter #(contains? component-names (:name %))
                                                components))))
        brick-ids-to-test (brick-names-to-ids bricks-to-test components)
        all-brick-ids (set (concat (map #(-> % :interface :name) components)
                                   (map :name bases)))
        brick-ids-in-project-src (brick-ids-in-project component-names-src base-names-src bricks)
        brick-ids-in-project-test (brick-ids-in-project (concat component-names-src component-names-test) (concat base-names-src base-names-test) bricks)
        test-only-brick-ids (set/difference brick-ids-in-project-test brick-ids-in-project-src)
        all-bricks (concat components bases)
        src-brick-id->brick-ids (into {} (map #(src-deps-from-namespaces % suffixed-top-ns) all-bricks))
        all-test-namespaces (all-test-namespaces all-bricks suffixed-top-ns)
        all-test-brick-id->brick-ids (into {} (map #(test-deps-from-namespaces % suffixed-top-ns all-test-namespaces src-brick-id->brick-ids) all-bricks))
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
      (let [src-brick-id (->brick-id brick)
            test-brick-id (str src-brick-id " (t)")]
        (doseq [brick-id (brick-id->brick-ids src-brick-id)]
          (update-deps! src-brick-id brick-id brick-id->brick-ids brick-id->deps #{src-brick-id}))
        (when (include-test? brick bricks-to-test)
          (doseq [brick-id (brick-id->brick-ids test-brick-id)]
            (update-deps! test-brick-id brick-id brick-id->brick-ids brick-id->deps #{test-brick-id})))))
    ;; Step 2: For each brick, convert interface names to component names + calculate missing and circular dependencies.
    (into {} (map (juxt :name #(brick-deps % @brick-id->deps ifc->comp all-brick-ids brick-ids-in-project-src brick-ids-in-project-test test-only-brick-ids brick-ids-to-test))
                  bricks))))
