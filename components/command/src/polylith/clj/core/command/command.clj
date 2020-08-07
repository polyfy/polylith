(ns polylith.clj.core.command.command)

(defn print-outside-ws-message []
  (println "  The command can only be executed from the workspace root."))
