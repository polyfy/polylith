(ns polylith.clj.core.poly-cli.core
  (:require [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.util.interface.exception :as ex]
            [polylith.clj.core.user-input.interface :as user-input])
  (:gen-class))

(defn -main [& args]
  "This is the entry point of the 'poly' command.

   It reads the input from the incoming arguments and delegates to the 'command' component,
   that reads the workspace in the read-workspace function in its core namespace:
     (-> user-input
         ws-clj/workspace-from-disk
         ws/enrich-workspace
         change/with-changes)

   - The first step ws-clj/workspace-from-disk reads the workspace from disk (or file if 'ws-file'
     is given).
   - The second step ws/enrich-workspace takes that hash map, which represents the whole workspace,
     as input and performs different kind of validations and enrichments. Worth noticing here
     is that all io operations are done in the first step while this step only operates on the
     hash map that was returned from the first step.
   - The last step change/with-changes, operates on the enhanced hash map, which again is an
     in-memory representation of the workspace. It calculates what components, bases and project
     that are changed, and what bricks and projects to test, and adds the result to the :changes key.
   - The final workspace representation is then used by the given command that is stored in
     the 'user-input' representation."
  (let [input (user-input/extract-params args)]
    (try
      (-> input command/execute-command System/exit)
      (catch Exception e
        (ex/print-exception e)
        (System/exit 1)))))
