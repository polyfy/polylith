(ns polylith.clj.core.deps.project-brick-deps
  (:require [clojure.set :as set]
            [polylith.clj.core.common.interface :as common]))

(defn update-deps! [next-brick-id [brick-id & path :as full-path] visited brick-id->deps]
  (when brick-id
    (let [{:keys [indirect circular completed?]} (@brick-id->deps brick-id)]
      (when (not completed?)
        (let [circular (if (seq circular)
                         circular
                         (if (= brick-id next-brick-id)
                           (conj (vec full-path) next-brick-id)
                           []))
              deps {:indirect (if (seq path)
                                (conj indirect next-brick-id)
                                indirect)
                    :circular circular
                    :completed? (or completed? (seq circular))}]
          (swap! brick-id->deps assoc brick-id deps)
          (when (seq path)
            (recur next-brick-id path visited brick-id->deps)))))))

(defn brick-deps-recursively
  ; todo: update
  "This is the core calculation of the dependencies for a specific namespace
   living in a brick for a specific project. The ns->namespaces is a bit misleading
   because it not only contains namespaces pointing to other namespaces, but also
   component interfaces and base (IB) names, pointing to both namespaces and other
   IB names."
  [brick-id brick-id->brick-ids brick-id->deps visited path]
  (let [depends-on-brick-ids (brick-id->brick-ids brick-id)]
    (update-deps! brick-id path visited brick-id->deps)
    (when (not (contains? visited brick-id))
      (doseq [depends-on-brick-id depends-on-brick-ids]
        (brick-deps-recursively depends-on-brick-id brick-id->brick-ids brick-id->deps
                                (conj visited brick-id)
                                (conj path brick-id))))
    (swap! brick-id->deps assoc-in [brick-id :completed?] true)))

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

(defn enhance-deps [brick-id brick-id->brick-ids brick-id->deps ifc->comp interface-and-base-names interface-and-base-names-in-project]
  (let [{:keys [indirect circular]} (@brick-id->deps brick-id)
        all-direct (set/intersection (set (brick-id->brick-ids brick-id)) interface-and-base-names)
        all-indirect (set/intersection indirect interface-and-base-names)
        direct (set/intersection all-direct interface-and-base-names-in-project)
        indirect (set/difference (set/intersection all-indirect interface-and-base-names-in-project) direct)
        missing-direct (set/difference all-direct interface-and-base-names-in-project)
        missing-indirect (set/difference indirect all-indirect)
        has-missing? (or (seq missing-direct) (seq missing-indirect))]
    (cond-> {}
            (seq direct) (assoc :direct (vec (sort (component-deps direct ifc->comp))))
            (seq indirect) (assoc :indirect (vec (sort (component-deps indirect ifc->comp))))
            has-missing? (assoc :missing-ifc-and-bases {:direct (-> missing-direct sort vec)
                                                        :indirect (-> missing-indirect sort vec)})
            (seq circular) (assoc :circular (vec (component-deps circular ifc->comp))))))

(defn merge-deps [{direct1 :direct indirect1 :indirect circular1 :circular}
                  {direct2 :direct indirect2 :indirect circular2 :circular}]
  (let [direct (vec (sort (concat direct1 direct2)))
        indirect (vec (sort (concat indirect1 indirect2)))
        circular (if (seq circular1) circular1 circular2)]
    (cond-> {}
            (seq direct) (assoc :direct direct)
            (seq indirect) (assoc :indirect indirect)
            (seq circular) (assoc :circular circular))))

(defn brick-deps
  "Calculates all dependencies for a given brick. To describe what's going on here, lets introduce
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
  [brick brick-id->deps ifc->comp bricks-to-test brick-id->brick-ids interface-and-base-names interface-and-base-names-in-project interface-and-base-names-in-project-test test-only-interfaces-and-bricks]
  (let [src-brick-id (brick-name-id brick)
        test-brick-id (str src-brick-id " (t)")
        _ (brick-deps-recursively src-brick-id brick-id->brick-ids brick-id->deps #{} [])
        src-deps (enhance-deps src-brick-id brick-id->brick-ids brick-id->deps ifc->comp interface-and-base-names interface-and-base-names-in-project)
        include-test? (include-test? brick bricks-to-test)
        test-deps (if include-test?
                    (do
                      (brick-deps-recursively test-brick-id brick-id->brick-ids brick-id->deps #{} [])
                      (enhance-deps test-brick-id brick-id->brick-ids brick-id->deps ifc->comp interface-and-base-names interface-and-base-names-in-project-test))
                    {})]
    (if (contains? test-only-interfaces-and-bricks src-brick-id)
      {:src {}
       :test (if include-test?
               (merge-deps src-deps test-deps)
               {})}
      {:src src-deps
       :test test-deps})))

(defn empty-dep [brick-id]
  [brick-id
   {:direct #{}
    :indirect #{}
    :circular []
    :completed? false}])

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
        interface-and-base-names (set (concat (map #(-> % :interface :name) components)
                                              (map :name bases)))
        interface-and-base-names-in-project (ifc-and-brick-names component-names-src base-names-src bricks)
        interface-and-base-names-in-project-test (ifc-and-brick-names (concat component-names-src component-names-test) (concat base-names-src base-names-test) bricks)
        test-only-interfaces-and-bricks (set/difference interface-and-base-names-in-project-test interface-and-base-names-in-project)
        all-bricks (concat components bases)
        src-brick-id->brick-ids (into {} (map #(src-brick-ids % suffixed-top-ns) all-bricks))
        all-test-namespaces (all-test-keys all-bricks suffixed-top-ns)
        all-test-brick-id->brick-ids (into {} (map #(test-brick-ids % suffixed-top-ns all-test-namespaces src-brick-id->brick-ids) all-bricks))
        brick-id->brick-ids (merge-with into src-brick-id->brick-ids all-test-brick-id->brick-ids)
        brick-id->deps (atom (into {} (map empty-dep (keys brick-id->brick-ids))))]
    (into {} (map (juxt :name #(brick-deps % brick-id->deps ifc->comp bricks-to-test brick-id->brick-ids interface-and-base-names interface-and-base-names-in-project interface-and-base-names-in-project-test test-only-interfaces-and-bricks))
                  bricks))))
