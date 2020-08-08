(ns polylith.clj.core.command.message)

(defn print-cant-be-executed-outside-ws []
  (println "  The command can only be executed from the workspace root."))
