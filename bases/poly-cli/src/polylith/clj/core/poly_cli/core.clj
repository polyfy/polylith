(ns ^:no-doc polylith.clj.core.poly-cli.core
  (:require [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.util.interface.exception :as ex]
            [polylith.clj.core.user-input.interface :as user-input])
  (:gen-class))

(defn -main
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
     in-memory representation of the workspace. It calculates what components, bases and projects
     that are changed, and what bricks and projects to test, and adds the result to the :changes key.
   - The final workspace representation is then used by the given command that is stored in
     the 'user-input' representation."
  [& args]
  (let [input (user-input/extract-arguments args)]
    (try
      (if (:is-no-exit input)
        (-> input command/execute-command)
        (-> input command/execute-command System/exit))
      (catch Exception e
        (ex/print-exception (:cmd input) e)
        (when (-> input :is-no-exit not)
          (System/exit 1))))))

(comment
  (-main "test" ":all" ":no-exit")
  (-main "help" "tap" ":no-exit")
  (-main "check" "ws-file:../sandbox/furkan.edn" ":no-exit")
  (-main "ws" "get:settings:vcs:polylith" ":latest-sha" ":no-exit")
  (-main "ws" "get:components:account:namespaces:src:core:file-path" "ws-file:../sandbox/furkan.edn" ":no-exit")
  (-main "ws" "get:components:api:namespaces:src:core:file-path" "ws-dir:../../Downloads/polylith-0.1.0-alpha9/" ":no-exit")
  (-main "check" ":no-exit")
  (-main "libs" ":update" "libraries:zprint/zprint" ":no-exit")
  (-main "libs" ":no-exit")
  (-main "libs" ":outdated" ":no-exit")
  (-main "doc" "page:polylith-ci-setup" ":no-exit")
  (-main "ws-dir:examples/doc-example" ":no-exit")
  (-main "overview" ":no-exit")
  (-main "check" "ws-dir:examples/local-dep" ":no-exit")
  (-main "ws" "get:components:without-src:non-top-namespaces" "ws-dir:examples/local-dep" ":no-exit")
  (-main "check" "ws-dir:examples/local-dep-old-format" ":no-exit")
  (-main "check" "ws-dir:../sandbox/ws35" ":no-exit")
  (-main "version" ":no-exit")
  (-main "shell" ":no-exit")
  #__)
