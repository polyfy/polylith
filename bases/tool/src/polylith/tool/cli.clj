(ns polylith.tool.cli
  (:require [polylith.cmd.interface :as cmd]
            [polylith.spec.interface :as spec]
            [polylith.workspace.interface :as ws]
            [polylith.workspace-clj.interface :as ws-clj]))

(def help-text
  (str
    "Allowed options:\n"
    "  compile [env] : Takes an optional environment name.\n"
    "                  If you run this without any arguments, it will\n"
    "                  compile all components and bases.\n"
    "                  If 'env' is given, it will compile all\n"
    "                  components and bases for that environment.\n"
    "\n"
    "  test env      : Runs tests for a given environment.\n"
    "                  You can define test sources and dependencies\n"
    "                  under an alias with the same name as the/\n"
    "                  environment you want to test in 'deps.edn'\n"
    "                  followed  by '-test', e.g. 'backend-test'\n."))

(defn -main [& [cmd env]]
  (let [ws-path "."
        workspace (-> ws-path
                      ws-clj/workspace-from-disk
                      ws/pimp-workspace)]
    (if-not (spec/valid-config? (:polylith workspace))
      (println "Expected to find a :polylith key in 'deps.edn'.")
      (try
        (case cmd
          "compile" (cmd/compile workspace env)
          "test" (cmd/test workspace env)
          (println help-text))
        (catch Exception e
          (println (or (-> e ex-data :err) (.getMessage e)))
          (System/exit (or (-> e ex-data :exit-code) 1)))
        (finally
          (System/exit 0))))))
