(ns polylith.clj.core.workspace-clj.project-settings)

(defn convert-test-fn [global-test]
  (fn convert-test [[k {:keys [test] :as v}]]
    [k
     (cond-> v
       (vector? test) (assoc :test {:include test})
       global-test (update :test #(merge global-test %)))]))

(defn or-default-constructor [candidate]
  (if (contains? #{nil :default} candidate)
    'polylith.clj.core.clojure-test-test-runner.interface/create
    candidate))

(defn ensure-test-runner-constructor [[project settings]]
  [project
   (update-in settings [:test :create-test-runner] or-default-constructor)])

(defn convert
  [{:keys [projects test]}]
  (into {}
        (comp (map (convert-test-fn test))
              (map ensure-test-runner-constructor))
        projects))
