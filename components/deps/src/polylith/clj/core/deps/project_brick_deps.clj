(ns polylith.clj.core.deps.project-brick-deps
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]))

(defn ns-deps-recursively
  "This is the core calculation of the dependencies for a specific namespace
   living in a brick for a specific project. The ns->namespaces is a bit misleading
   because it not only contains namespaces pointing to other namespaces, but also
   component interfaces and base (IB) names, pointing to both namespaces and other
   IB names."
  [current-ns ns->namespaces brick-paths visited path]
  (let [namespaces (ns->namespaces current-ns)]
    (if (or (empty? namespaces)
            (contains? visited current-ns))
      (swap! brick-paths conj
             (conj path current-ns))
      (doseq [namespace namespaces]
        (ns-deps-recursively namespace ns->namespaces brick-paths
                             (conj visited current-ns)
                             (conj path current-ns))))))

(defn short-ns [namespace suffixed-top-ns test-namespaces]
  (let [{:keys [root-ns depends-on-ns]} (common/extract-namespace suffixed-top-ns namespace)
        short-ns (str root-ns "." depends-on-ns)]
    (when root-ns
      (if (contains? test-namespaces short-ns)
        (str short-ns " (t)")
        root-ns))))

(defn interface-name [{:keys [namespace]} suffixed-top-ns]
  (let [{:keys [root-ns]} (common/extract-namespace suffixed-top-ns namespace)]
    root-ns))

(defn filter-component-ns [namespaces suffixed-top-ns]
  (filterv #(str/starts-with? % suffixed-top-ns) namespaces))

(defn brick-namespace [{:keys [type name interface]}]
  (case type
    "base" name
    "component" (:name interface)))

(defn test-key [{:keys [namespace]} suffixed-top-ns]
  (let [{:keys [root-ns depends-on-ns]} (common/extract-namespace suffixed-top-ns namespace)]
    (when root-ns
      [(str root-ns "." depends-on-ns)])))

(defn test-keys [{:keys [namespaces]} suffixed-top-ns]
  (mapcat #(test-key % suffixed-top-ns) (:test namespaces)))

(defn all-test-keys [components suffixed-top-ns]
  (set (mapcat #(test-keys % suffixed-top-ns) components)))

(defn src-namespaces [{:keys [namespaces] :as brick} suffixed-top-ns]
  (let [brick-ns (brick-namespace brick)]
    [brick-ns
     (sort (set (filter #(and (not= brick-ns %)
                              (-> % nil? not))
                        (map #(:root-ns (common/extract-namespace suffixed-top-ns %))
                             (mapcat :imports (:src namespaces))))))]))

(defn test-ns->namespaces [{:keys [namespaces]} suffixed-top-ns test-namespaces]
  (into {} (filter first (map (juxt #(short-ns (:namespace %) suffixed-top-ns test-namespaces)
                                    #(set (filter identity (map (fn [n] (short-ns n suffixed-top-ns test-namespaces))
                                                                (:imports %)))))
                              (:test namespaces)))))

(defn brick-test-namespaces [{:keys [namespaces]} suffixed-top-ns test-namespaces]
  (map #(short-ns (:namespace %) suffixed-top-ns test-namespaces) (:test namespaces)))

(defn all-brick-namespaces [brick suffixed-top-ns test-namespaces]
  (set (conj (brick-test-namespaces brick suffixed-top-ns test-namespaces)
             (brick-namespace brick))))

(defn circular? [namespaces]
  (not= (count namespaces)
        (-> namespaces set count)))

(defn test? [[namespace _]]
  (str/ends-with? namespace " (t)"))

(defn test-ns-suffix [brick-ns]
  (if (str/ends-with? brick-ns " (t)")
    " (t)"
    ""))

(defn clean-ns [ns-name]
  (let [test-suffix (test-ns-suffix ns-name)
        idx (str/index-of ns-name ".")]
    (if idx
      (str (subs ns-name 0 idx) test-suffix)
      ns-name)))

(defn extract-name [path]
  (if-let [idx (str/index-of path ".")]
    (subs path 0 idx)))

(defn clean-nss [ns-names]
  (let [cleaned-ns-names (map clean-ns ns-names)
        brick-ns (first cleaned-ns-names)]
    (conj (drop-while #(= brick-ns %)
                      cleaned-ns-names)
          brick-ns)))

(defn drop-brick-ns [ns-names src-test-brick-ns]
  (drop-while #(contains? src-test-brick-ns %)
              ns-names))

(defn component-deps [deps ifc->comp]
  (map #(ifc->comp % %) deps))

(defn component-deps [deps ifc->comp]
  (map #(ifc->comp % %) deps))

(defn ifc-names [component-names bricks]
  (set (map #(-> % :interface :name)
            (filter #(contains? (set component-names) (:name %))
                    bricks))))

(defn source-deps
  "Takes a sequence of namespace paths and calculates direct, indirect, and circular
   dependencies + dependencies on missing interfaces (if any). All incoming dependencies
   are on interfaces, but are then translated to corresponding components, using the
   ifc->comp map that is based on the components in the project for which this
   calculation operates on."
  [ns-paths ifc->comp interface-names src-test-brick-ns]
  (let [circular (first (sort-by count (filter circular? ns-paths)))
        paths (map #(drop-brick-ns % src-test-brick-ns)
                   (set (map clean-nss ns-paths)))
        direct-and-indirect (set (flatten paths))
        all-direct (set (filter identity (map first paths)))
        direct (set/intersection all-direct interface-names)
        missing-ifc (set/difference all-direct interface-names)
        all-indirect (set/difference direct-and-indirect all-direct)
        indirect (set/intersection all-indirect interface-names)
        indirect-missing-ifc (set/difference indirect all-indirect)
        has-missing-ifc? (or (seq missing-ifc) (seq indirect-missing-ifc))]
    (cond-> {}
            (seq direct) (assoc :direct (vec (sort (component-deps direct ifc->comp))))
            (seq indirect) (assoc :indirect (vec (sort (component-deps indirect ifc->comp))))
            has-missing-ifc? (assoc :missing-ifc {:direct (-> missing-ifc sort vec)
                                                  :indirect (-> indirect-missing-ifc sort vec)})
            (seq circular) (assoc :circular (vec (component-deps circular ifc->comp))))))

(defn include-test?
  "Checks if the brick is included in workspace.edn > :projects > PROJECT-KEY > :test.
   If the :test key is not present, then it is treated as included."
  [{:keys [name]} bricks-to-test]
  (or (nil? bricks-to-test)
      (contains? bricks-to-test name)))

(defn brick-deps
  "Calculates all dependencies for a given brick. To describe what's going on here, lets introduce
   a few abbreviations:
     IB = Component interface name, e.g. 'util', or base name, e.g. 'poly-cli'.
     SN = Short namespace name (only used by the tests right now), e.g. 'util.util-test'
          where 'util' in this case is an IB and 'util-test' is a top namespace within
          that brick (a brick with the interface 'util' in this case).

   The 'all-src-ns->namespaces' map has an IB as key, and a sequence of component
   interfaces as a value for each key (components and bases can't depend on bases).

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
  [brick components bases suffixed-top-ns ifc->comp interface-names interface-names-test bricks-to-test]
  (let [brick-ns (brick-namespace brick)
        src-test-brick-ns #{brick-ns (str brick-ns " (t)")}
        bricks (concat components bases)
        test-namespaces (all-test-keys bricks suffixed-top-ns)
        all-src-ns->namespaces (into {} (map #(src-namespaces % suffixed-top-ns) bricks))
        all-test-ns->namespaces (into {} (apply merge (map #(test-ns->namespaces % suffixed-top-ns test-namespaces) bricks)))
        all-ns->namespaces (merge-with into all-src-ns->namespaces all-test-ns->namespaces)
        namespaces (all-brick-namespaces brick suffixed-top-ns test-namespaces)
        brick-paths (atom [])
        _ (doseq [namespace namespaces]
            (ns-deps-recursively namespace all-ns->namespaces brick-paths #{} []))
        src-paths (filter (complement test?) @brick-paths)
        test-paths (filter test? @brick-paths)
        src-deps (source-deps src-paths ifc->comp interface-names src-test-brick-ns)]
    {:src src-deps
     :test (if (include-test? brick bricks-to-test)
             (source-deps test-paths ifc->comp interface-names-test src-test-brick-ns)
             {})}))

(defn project-deps
  "Calculate the source and test dependencies for a project. The returned dependencies
   are stored in a map with a :src and :test key and includes a key for each brick that is included
   in the project together with the direct, indirect, and circular dependencies (if any) +
   missing dependencies on interfaces."
  [components bases component-names-src component-names-test base-names-src base-names-test suffixed-top-ns bricks-to-test]
  (let [brick-names (set (concat component-names-src component-names-test base-names-src base-names-test))
        bricks (filter #(contains? brick-names (:name %))
                       (concat bases components))
        ifc->comp (into {} (map (juxt #(-> % :interface :name) :name) components))
        interface-names (ifc-names component-names-src bricks)
        interface-names-test (ifc-names (concat component-names-src component-names-test) bricks)]
    (into {} (map (juxt :name #(brick-deps % components bases suffixed-top-ns ifc->comp interface-names interface-names-test bricks-to-test))
                  bricks))))
