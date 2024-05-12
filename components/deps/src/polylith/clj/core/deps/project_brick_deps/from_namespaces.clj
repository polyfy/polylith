(ns polylith.clj.core.deps.project-brick-deps.from-namespaces
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.project-brick-deps.shared :as shared]))

(defn extract-test-deps-from-namespace
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

(defn extract-test-deps
  "Iterates through all namespaces for a brick's test context and calculates its dependencies."
  [{:keys [namespaces] :as brick} suffixed-top-ns all-test-namespaces src-brick-id->brick-ids]
  (let [plain-brick-id (shared/->brick-id brick)
        brick-id (str plain-brick-id " (t)")]
    [brick-id
     (vec (sort (set (filter #(and (not= brick-id %)
                                   (-> % nil? not))
                             (mapcat #(extract-test-deps-from-namespace suffixed-top-ns % plain-brick-id all-test-namespaces src-brick-id->brick-ids)
                                     (mapcat :imports (:test namespaces)))))))]))

(defn extract-src-deps
  "Iterates through all namespaces for a brick's src context and calculates its dependencies."
  [{:keys [namespaces] :as brick} suffixed-top-ns]
  (let [brick-id (shared/->brick-id brick)]
    [brick-id
     (vec (sort (set (filter #(and (not= brick-id %)
                                   (-> % nil? not))
                             (map #(:root-ns (common/extract-namespace suffixed-top-ns %))
                                  (mapcat :imports (:src namespaces)))))))]))
