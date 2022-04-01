(ns polylith.clj.core.workspace-clj.project-settings)

(defn convert-test-fn [global-test]
  (fn convert-test [[k {:keys [test] :as v}]]
    [k
     (cond-> v
       (vector? test) (assoc :test {:include test})
       global-test (update :test #(merge global-test %)))]))

(defn convert
  [{:keys [projects test]}]
  (if projects
    (into {} (map (convert-test-fn test)) projects)
    {}))
