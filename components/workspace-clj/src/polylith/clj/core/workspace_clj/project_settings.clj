(ns ^:no-doc polylith.clj.core.workspace-clj.project-settings)

(defn convert-test [global-test {:keys [test] :as project-settings}]
  (cond-> project-settings

          ;; Convert from the old test configuration format to the new one.
          (vector? test)
          (assoc :test {:include test})

          ;; Merge with the global test configuration.
          global-test
          (update :test #(merge global-test %))))

(defn convert
  "This function converts from the old test settings format to the new
  one for backward compatibility. It also adds the global test config as
  the project's test config if it is missing the test keyword and there
  is a global test configuration."
  [{:keys [projects test]}]
  (reduce (fn [acc [project-name settings]]
            (assoc acc project-name
                       (convert-test test settings)))
          {}
          projects))
