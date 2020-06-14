(ns polylith.tool.cli
  (:require [clojure.java.io :as io]
            [polylith.cmd.interface :as cmd]
            [polylith.spec.interface :as spec]))

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
  (let [ws-path (.getAbsolutePath (io/file ""))
        config (-> "deps.edn" slurp read-string)]
    (if-not (spec/valid-config? (:polylith config))
      (println "The 'deps.edn' file should contain a polylith config map with a :polylith key.")
      (try
        (case cmd
          "compile" (cmd/compile ws-path config env)
          "test" (cmd/test ws-path config env)
          (println help-text))
        (catch Exception e
          (println (or (-> e ex-data :err) (.getMessage e)))
          (System/exit (or (-> e ex-data :exit-code) 1)))
        (finally
          (System/exit 0))))))
