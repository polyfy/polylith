(ns polylith.tool.poly
  (:require [polylith.spec.interface :as spec]
            [polylith.test.interface :as test]
            [polylith.workspace.interface :as ws]
            [polylith.workspace-clj.interface :as ws-clj]
            [clojure.java.io :as io]))

(def help-text
  (str
    "Commands:\n"
    "  test env      : Runs tests for a given environment.\n"
    "                  You can define test sources and dependencies\n"
    "                  under an alias with the same name as the/\n"
    "                  environment you want to test in 'deps.edn'\n"
    "                  followed by '-test', e.g. 'backend-test'\n."))

(defn -main [& [cmd env]]
  (let [ws-path (.getAbsolutePath (io/file ""))
        workspace (-> ws-path
                      ws-clj/workspace-from-disk
                      ws/pimp-workspace)]
    (if-not (spec/valid-config? (:settings workspace))
      (println "Expected to find a :polylith key in 'deps.edn'.")
      (try
        (case cmd
          "test" (test/run workspace env)
          (println help-text))
        (catch Exception e
          (println (or (-> e ex-data :err) (.getMessage e)))
          (System/exit (or (-> e ex-data :exit-code) 1)))
        (finally
          (System/exit 0))))))
