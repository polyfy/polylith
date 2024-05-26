(ns polylith.clj.core.deps.project-brick-deps.from-namespaces
  (:require [polylith.clj.core.common.interface :as common]))

(defn extract-test-deps-from-imports
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

(defn with-alias [brick-id alias]
  (when brick-id
    (if alias
      (str alias "/" brick-id)
      brick-id)))

(defn extract-test-deps-from-nss
  "Iterates through all namespaces for a brick's test context and calculates its dependencies."
  [{:keys [brick-id namespaces]}
   {:keys [alias suffixed-top-ns]} all-test-namespaces src-brick-id->brick-ids]
  (vec (sort (set
               (map #(with-alias % alias)
                    (filter #(and (not= brick-id %)
                                  (-> % nil? not))
                            (mapcat #(extract-test-deps-from-imports suffixed-top-ns % brick-id all-test-namespaces src-brick-id->brick-ids)
                                    (mapcat :imports (:test namespaces)))))))))

(defn sort-and-filter [brick-id brick-ids]
  (vec (sort (set (filter #(and (not= brick-id %)
                                (-> % nil? not))
                          brick-ids)))))

(defn extract-test-deps
  "Iterates through all namespaces for a brick's test context and calculates its dependencies."
  [{:keys [brick-id] :as brick} wss all-test-namespaces src-brick-id->brick-ids]
  (let [brick-id (str brick-id " (t)")]
    [brick-id
     (sort-and-filter brick-id
                      (mapcat #(extract-test-deps-from-nss brick % all-test-namespaces src-brick-id->brick-ids)
                              wss))]))

(defn extract-src-deps-from-ns
  [{:keys [brick-id namespaces]}
   {:keys [alias suffixed-top-ns]}]
  (filter #(and (not= brick-id %)
                (-> % nil? not))
          (map #(with-alias (:root-ns (common/extract-namespace suffixed-top-ns %)) alias)
               (mapcat :imports (:src namespaces)))))

(defn extract-src-deps
  "Iterates through all namespaces for a brick's src context and calculates its dependencies."
  [{:keys [brick-id] :as brick} wss]
  [brick-id
   (sort-and-filter brick-id
                    (mapcat #(extract-src-deps-from-ns brick %)
                            wss))])
