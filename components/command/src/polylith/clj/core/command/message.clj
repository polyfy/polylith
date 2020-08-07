(ns polylith.clj.core.command.message)

(defn print-dont-execute-outside-ws []
  (println "  The command can only be executed from the workspace root."))
