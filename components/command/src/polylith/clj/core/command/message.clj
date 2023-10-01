(ns ^:no-doc polylith.clj.core.command.message)

(defn cant-be-executed-outside-ws-message [cmd]
  (if (= "test" cmd)
    "  The command can only be executed from the workspace root."
    "  The command can only be executed from the workspace root, or by also passing in :: or ws-dir:DIR."))
