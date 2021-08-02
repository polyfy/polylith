(ns polylith.clj.core.workspace-clj.project-settings)

(defn convert-test [[k {:keys [test] :as v}]]
  (if (vector? test)
    [k (assoc v :test {:include test})]
    [k v]))

(defn convert
  [{:keys [projects]}]
  (if projects
    (into {} (map convert-test projects))
    {}))
