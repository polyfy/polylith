(ns polylith.clj.core.command.test
  (:require [polylith.clj.core.test-runner.interfc :as test-runner]))

(defn validate [unnamed-args]
  (if (-> unnamed-args empty? not)
    {:message "  Arguments should be passed by name, e.g.: test env:my-env"}
    {:ok? true}))

(defn args [env all all-bricks]
  {:run-all? (or (= "true" all)
                 (= "true" all-bricks))
   :run-env-tests? (or (= "true" all)
                       (= "true" env))})

(defn run [workspace env all all-bricks unnamed-args]
  (let [{:keys [ok? message]} (validate unnamed-args)]
    (if ok?
      (let [{:keys [run-all? run-env-tests?]} (args env all all-bricks)]
        (test-runner/run workspace env run-all? run-env-tests?))
      (println message))))
