(ns polylith.clj.core.command.test
  (:require [polylith.clj.core.test-runner.interfc :as test-runner]))

(defn validate [unnamed-args]
  (if (-> unnamed-args empty? not)
    {:message "  Arguments should be passed by name, e.g.: test env:dev"}
    {:ok? true}))

(defn args [env all all-bricks]
  {:run-all? (or (= "true" all)
                 (= "true" all-bricks))
   :run-env-tests? (or (= "true" all)
                       (= "true" env))})

(defn run [workspace env unnamed-args]
  (let [{:keys [ok? message]} (validate unnamed-args)]
    (if ok?
      (test-runner/run workspace env)
      (println message))))
