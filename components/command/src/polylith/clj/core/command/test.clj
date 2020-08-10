(ns polylith.clj.core.command.test
  (:require [polylith.clj.core.test-runner.interfc :as test-runner]))

(defn validate [unnamed-args]
  (if (-> unnamed-args empty? not)
    {:message "  Arguments should be passed by name, e.g.: test env:dev"}
    {:ok? true}))

(defn run [workspace unnamed-args]
  (let [{:keys [ok? message]} (validate unnamed-args)]
    (if ok?
      (test-runner/run workspace)
      (println message))))
