(ns polylith.clj.core.deps.project-brick-deps
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]))

(defn add-child-paths [{:keys [paths]} child-brick-id child-paths]
   (let [child-paths (if (empty? child-paths)
                       [[child-brick-id]]
                       (mapv #(vec (cons child-brick-id %)) child-paths))]
     (concat paths child-paths)))

(defn transfer-deps [brick-id child-brick-id brick-id->deps]
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

(defn update-indirect-deps!
  [parent-brick-id brick-id brick-id->brick-ids brick-id->deps visited]
  (if (get-in @brick-id->deps [brick-id :completed?])
    (swap! brick-id->deps assoc parent-brick-id
           (transfer-deps parent-brick-id brick-id @brick-id->deps))
    (do
      (when (not (contains? visited brick-id))
        (let [brick-ids (brick-id->brick-ids brick-id)]
          (if (empty? brick-ids)
            (swap! brick-id->deps assoc brick-id
                   (assoc (@brick-id->deps brick-id) :completed? true))
            (doseq [depends-on-brick-id brick-ids]
              (update-indirect-deps! brick-id depends-on-brick-id brick-id->brick-ids brick-id->deps
                                     (conj visited brick-id))))))
      (swap! brick-id->deps assoc parent-brick-id
             (update-dep parent-brick-id brick-id @brick-id->deps)))))

(defn brick-name-id [{:keys [type name interface]}]
  (case type
    "base" name
    "component" (:name interface)))

(defn test-key [{:keys [namespace]} suffixed-top-ns]
  (let [{:keys [root-ns depends-on-ns]} (common/extract-namespace suffixed-top-ns namespace)]
    (when root-ns
      [(str root-ns "." depends-on-ns)])))

(defn test-keys [{:keys [namespaces]} suffixed-top-ns]
  (mapcat #(test-key % suffixed-top-ns) (:test namespaces)))

(defn all-test-keys [bricks suffixed-top-ns]
  (set (mapcat #(test-keys % suffixed-top-ns) bricks)))

(defn src-brick-ids [{:keys [namespaces] :as brick} suffixed-top-ns]
  (let [brick-id (brick-name-id brick)]
    [brick-id
     (vec (sort (set (filter #(and (not= brick-id %)
                                   (-> % nil? not))
                             (map #(:root-ns (common/extract-namespace suffixed-top-ns %))
                                  (mapcat :imports (:src namespaces)))))))]))

(defn test-brick [suffixed-top-ns ns-to-extract brick-id all-test-namespaces src-brick-id->brick-ids]
  (let [{:keys [root-ns depends-on-ns]} (common/extract-namespace suffixed-top-ns ns-to-extract)]
    (if (contains? all-test-namespaces (str root-ns "." depends-on-ns))
      [(str root-ns " (t)")]
      (if (= brick-id root-ns)
        ;; if the test context depends on the src context of itself, use the dependencies from the src context.
        (src-brick-id->brick-ids brick-id)
        [root-ns]))))

(defn test-brick-ids [{:keys [namespaces] :as brick} suffixed-top-ns all-test-namespaces src-brick-id->brick-ids]
  (let [plain-brick-id (brick-name-id brick)
        brick-id (str plain-brick-id " (t)")]
    [brick-id
     (vec (sort (set (filter #(and (not= brick-id %)
                                   (-> % nil? not))
                             (mapcat #(test-brick suffixed-top-ns % plain-brick-id all-test-namespaces src-brick-id->brick-ids)
                                     (mapcat :imports (:test namespaces)))))))]))

(defn component-deps [deps ifc->comp]
  (map #(ifc->comp % %) deps))

(defn ifc-and-brick-names [component-names base-names bricks]
  (set (concat (map #(-> % :interface :name)
                    (filter #(contains? (set component-names) (:name %))
                            bricks))
               (map :name
                    (filter #(contains? (set base-names) (:name %))
                            bricks)))))

(defn include-test?
  "Checks if the brick is included in workspace.edn > :projects > PROJECT-KEY > :test > :include.
   If the :test key is not present, then it is treated as included."
  [{:keys [name]} bricks-to-test]
  (or (nil? bricks-to-test)
      (contains? bricks-to-test name)))

(defn drop-suffix [brick-id]
  (first (str/split brick-id #" ")))

(defn drop-suffixes
  "Drop the (t) suffix (if any). We don't keept track of whether we depend on the src or test context,
   only if we depend from src or test context."
  [brick-ids]
  (set (map drop-suffix brick-ids)))

(defn circular-dep [brick-id indirect paths]
  (when (contains? indirect brick-id)
    (vec (cons brick-id
               (first (filterv #(= brick-id (last %))
                               paths))))))

(defn missing-deps [brick-ids all-brick-ids brick-ids-to-ignore-if-missing]
  (set/difference (if brick-ids-to-ignore-if-missing
                    (set/intersection brick-ids brick-ids-to-ignore-if-missing)
                    brick-ids)
                  all-brick-ids))

(defn finalise-deps [brick-id brick-id->deps ifc->comp all-brick-ids brick-ids-in-project brick-ids-to-concider-if-missing]
  (let [{:keys [direct indirect paths]} (brick-id->deps brick-id)
        circular (circular-dep brick-id indirect paths)
        all-direct (set/intersection (drop-suffixes direct) all-brick-ids)
        all-indirect (set/intersection (drop-suffixes indirect) all-brick-ids)
        direct (set/intersection all-direct brick-ids-in-project)
        indirect (set/difference (set/intersection all-indirect brick-ids-in-project) direct)
        missing-direct (missing-deps all-direct brick-ids-in-project brick-ids-to-concider-if-missing)
        missing-indirect (missing-deps indirect all-indirect brick-ids-to-concider-if-missing)
        has-missing? (or (seq missing-direct) (seq missing-indirect))]
    (cond-> {}
            (seq direct) (assoc :direct (vec (sort (component-deps direct ifc->comp))))
            (seq indirect) (assoc :indirect (vec (sort (component-deps indirect ifc->comp))))
            has-missing? (assoc :missing-ifc-and-bases {:direct (-> missing-direct sort vec)
                                                        :indirect (-> missing-indirect sort vec)})
            (seq circular) (assoc :circular (vec (component-deps circular ifc->comp))))))

(defn merge-missing [{src1 :src test1 :test}
                     {src2 :src test2 :test}]
  (let [src (vec (concat src1 src2))
        test (vec (concat test1 test2))]
    [{:src src
      :test test}
     (or (seq src)
         (seq test))]))

(defn merge-source-deps
  "If a brick is only used from the test context, then all its dependencies (src + test)
   are treated as they are test dependencies, and in that case we need to merge them."
  [{direct1 :direct indirect1 :indirect circular1 :circular missing-ifc-and-bases1 :missing-ifc-and-bases}
   {direct2 :direct indirect2 :indirect circular2 :circular missing-ifc-and-bases2 :missing-ifc-and-bases}]
  (let [direct (vec (sort (concat direct1 direct2)))
        indirect (vec (sort (concat indirect1 indirect2)))
        circular (if (seq circular1) circular1 circular2)
        [missing missing?] (merge-missing missing-ifc-and-bases1 missing-ifc-and-bases2)]
    (cond-> {}
            (seq direct) (assoc :direct direct)
            (seq indirect) (assoc :indirect indirect)
            missing? (assoc :missing-ifc-and-bases missing)
            (seq circular) (assoc :circular circular))))

(defn brick-deps
  "...we start with calculating the src dependencies and because it can only depend on src context,
   we can finish the calculations for src. ...

   Calculates all dependencies for a given brick. To describe what's going on here, lets introduce
   a few abbreviations:
     IB = Component interface name, e.g. 'util', or base name, e.g. 'poly-cli'.
     SN = Short namespace name (only used by the tests right now), e.g. 'util.util-test'
          where 'util' in this case is an IB and 'util-test' is a top namespace within
          that brick (a brick with the interface 'util' in this case).

   The 'all-src-ns->namespaces' map has an IB as key, and a sequence of IB:s as a value
   for each key (bases can depend on bases).

   The 'all-test-ns->namespaces' map has an SN as key, and a sequence of IB's and SN's
   as a value for each key. If depending on another test namespace, either within its own
   brick or another brick's test namespace (which is allowed) then the value is an
   SN, but if depending on a component's namespace, then the value will be the component
   interface name, regardless if it's an interface sub namespace (e.g. 'util.interface.str')
   or just a normal top interface, e.g. 'util.interface'. The two maps are then merged into
   'all-ns->namespaces'.

   The 'namespaces' is then populated with the brick's IB and test namespaces.

   The 'all-ns->namespaces' map is then passed into the dependency calculation together with
   one namespace from the 'namespaces' at a time as a starting point.

   The 'src' dependencies are then calculated, and also the 'test' dependencies if the
   brick is not excluded in workspace.edn > :projects > PROJECT-KEY > :test."
  ;"Takes a sequence of namespace paths and calculates direct, indirect, and circular
  ; dependencies + dependencies on missing interfaces (if any). All incoming dependencies
  ; are bases and interfaces, but the latter is translated to corresponding components,
  ; using the ifc->comp map that is based on the components in the project for which this
  ; calculation operates on."

  ;; todo update
  ;"Calculate the source and test dependencies for a project. The returned dependencies
  ; are stored in a map with a :src and :test key and includes a key for each brick that is included
  ; in the project together with the direct, indirect, and circular dependencies (if any) +
  ; missing dependencies on interfaces."
  [brick brick-id->deps ifc->comp all-brick-ids brick-ids-in-project brick-ids-in-project-test test-only-brick-ids brick-ids-to-test]
  (let [src-brick-id (brick-name-id brick)
        test-brick-id (str src-brick-id " (t)")
        src-deps (finalise-deps src-brick-id @brick-id->deps ifc->comp all-brick-ids brick-ids-in-project nil)
        test-deps (finalise-deps test-brick-id @brick-id->deps ifc->comp all-brick-ids brick-ids-in-project-test brick-ids-to-test)]
    (if (contains? test-only-brick-ids src-brick-id)
      {:src  {}
       :test (merge-source-deps src-deps test-deps)}
      {:src  src-deps
       :test test-deps})))

(defn empty-dep [brick-id brick-id->brick-ids]
  [brick-id
   {:direct (brick-id->brick-ids brick-id)
    :indirect #{}
    :paths []}])

(defn brick-ids-to-test [bricks-to-test components]
  (when bricks-to-test
    (let [name->brick-id (map (fn [{:keys [name interface]}] [name (:name interface)])
                              components)]
      (set (map #(name->brick-id % %) bricks-to-test)))))

(defn project-deps
  ;; todo: update
  "Calculate the source and test dependencies for a project. The returned dependencies
   are stored in a map with a :src and :test key and includes a key for each brick that is included
   in the project together with the direct, indirect, and circular dependencies (if any) +
   missing dependencies on interfaces."
  [components bases component-names-src component-names-test base-names-src base-names-test suffixed-top-ns bricks-to-test]
  (let [brick-names (set (concat component-names-src component-names-test base-names-src base-names-test))
        bricks (filter #(contains? brick-names (:name %))
                       (concat bases components))
        component-names (set (concat component-names-src component-names-test))
        ;; Make sure we pick the right component if more than one for an interface.
        ifc->comp (into {} (map (juxt #(-> % :interface :name) :name)
                                (concat components
                                        (filter #(contains? component-names (:name %))
                                                components))))
        brick-ids-to-test (brick-ids-to-test bricks-to-test components)
        all-brick-ids (set (concat (map #(-> % :interface :name) components)
                                   (map :name bases)))
        brick-ids-in-project (ifc-and-brick-names component-names-src base-names-src bricks)
        brick-ids-in-project-test (ifc-and-brick-names (concat component-names-src component-names-test) (concat base-names-src base-names-test) bricks)
        test-only-brick-ids (set/difference brick-ids-in-project-test brick-ids-in-project)
        all-bricks (concat components bases)
        src-brick-id->brick-ids (into {} (map #(src-brick-ids % suffixed-top-ns) all-bricks))
        all-test-namespaces (all-test-keys all-bricks suffixed-top-ns)
        all-test-brick-id->brick-ids (into {} (map #(test-brick-ids % suffixed-top-ns all-test-namespaces src-brick-id->brick-ids) all-bricks))
        brick-id->brick-ids (merge-with into src-brick-id->brick-ids all-test-brick-id->brick-ids)
        brick-id->deps (atom (into {} (map #(empty-dep % brick-id->brick-ids)
                                           (keys brick-id->brick-ids))))]
    (doseq [brick bricks]
      (let [src-brick-id (brick-name-id brick)
            test-brick-id (str src-brick-id " (t)")]
        (doseq [brick-id (brick-id->brick-ids src-brick-id)]
          (update-indirect-deps! src-brick-id brick-id brick-id->brick-ids brick-id->deps #{src-brick-id}))
        (when (include-test? brick bricks-to-test)
          (doseq [brick-id (brick-id->brick-ids test-brick-id)]
            (update-indirect-deps! test-brick-id brick-id brick-id->brick-ids brick-id->deps #{test-brick-id})))))
    (into {} (map (juxt :name #(brick-deps % brick-id->deps ifc->comp all-brick-ids brick-ids-in-project brick-ids-in-project-test test-only-brick-ids brick-ids-to-test))
                  bricks))))
