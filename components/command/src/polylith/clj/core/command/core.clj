(ns ^:no-doc polylith.clj.core.command.core
  (:require [clojure.string :as str]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.check.interface :as check]
            [polylith.clj.core.command.cmd-validator.core :as cmd-validator]
            [polylith.clj.core.command.create :as create]
            [polylith.clj.core.command.dependencies :as dependencies]
            [polylith.clj.core.command.exit-code :as exit-code]
            [polylith.clj.core.command.info :as info]
            [polylith.clj.core.command.test :as test]
            [polylith.clj.core.command.user-config :as user-config]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.config-reader.interface :as config-reader]
            [polylith.clj.core.doc.interface :as doc]
            [polylith.clj.core.help.interface :as help]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.overview.interface :as overview]
            [polylith.clj.core.shell.interface :as shell]
            [polylith.clj.core.tap.interface :as tap]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.version.interface :as ver]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.workspace.interface :as workspace]
            [polylith.clj.core.ws-file.interface :as ws-file]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer])
  (:refer-clojure :exclude [test]))

(defn diff [workspace]
  (doseq [file (-> workspace :changes :changed-files)]
    (println file)))

(defn open-help [[_ cmd ent] is-show-project is-show-brick is-show-workspace fake-poly? color-mode]
  (help/print-help cmd ent is-show-project is-show-brick is-show-workspace fake-poly? color-mode))

(defn version []
  (println (str "  " (common/version-name false) " (" ver/date ")")))

(defn unknown-command [cmd]
  (println (str "  Unknown command '" cmd "'. Type 'poly help' for help.")))

(defn read-workspace [ws-file user-input]
  (if ws-file
    (ws-file/read-ws-from-file ws-file user-input)
    (-> user-input
        ws-clj/workspace-from-disk
        workspace/enrich-workspace
        change/with-changes)))

(defn workspace-reader-fn []
  (fn [user-input ws-file]
    (read-workspace ws-file user-input)))

(defn with-switch [cmd user-input]
  (let [[switch path] (when cmd (str/split cmd #":"))]
    (if (contains? #{"ws-dir" "ws-file"} switch)
      ["shell" (assoc user-input (keyword switch) path)]
      [cmd user-input])))

(defn with-shell
  "This function allows us to open a shell by skipping the word 'shell', e.g.:
     poly :tap
     poly :github
     poly ws-dir:examples/doc-example
     poly ws-file:aproject.edn"
  [cmd user-input]
  (let [[cmd user-input] (with-switch cmd user-input)]
    (condp = cmd
      nil ["shell" user-input]
      "shell" ["shell" user-input]
      "ws-dir" ["shell" user-input]
      ":all" ["shell" (assoc user-input :is-all true)]
      ":tap" ["shell" (assoc user-input :is-tap true)]
      ":fake-poly" ["shell" (assoc user-input :is-fake-poly true)]
      ":github" ["shell" (assoc user-input :is-github true)]
      ":local" ["shell" (assoc user-input :is-local true)]
      [cmd user-input])))

(defn libs [workspace is-update]
  (if is-update
    (lib/update-libs! workspace)
    (lib/print-lib-table workspace)))

(defn print-deprecation-message [color-mode]
  (println (str "  The use of :: is " (color/error color-mode "deprecated") " and support for it will probably be dropped in the future. "
                "Please contact the Polylith team if you think it's important to keep!")))

(defn execute [{:keys [cmd args alias name top-ns branch help is-local more page ws is-tap is-git-add is-github is-commit is-update is-show-brick is-show-workspace is-show-project is-verbose is-fake-poly is-search-for-ws-dir get out interface selected-bricks selected-projects unnamed-args ws-file] :as user-input}]
  (let [color-mode (common/color-mode user-input)
        ws-dir (config-reader/workspace-dir user-input)
        workspace-fn (workspace-reader-fn)
        workspace (workspace-fn user-input ws-file)
        [cmd user-input] (with-shell cmd user-input)]
    (user-config/create-user-config-if-not-exists)
    (when is-tap (tap/execute "open"))
    (when is-search-for-ws-dir (print-deprecation-message color-mode))
    (let [brick-name (first selected-bricks)
          project-name (first selected-projects)
          test-result (atom true)
          [ok? message] (cmd-validator/validate workspace user-input color-mode)]
      (if ok?
        (case cmd
          "check" (check/print-check workspace color-mode)
          "create" (create/create ws-dir workspace args name alias top-ns interface branch is-git-add is-commit color-mode)
          "deps" (dependencies/deps workspace project-name brick-name unnamed-args)
          "doc" (doc/open-doc branch is-local is-github help more page ws unnamed-args)
          "diff" (diff workspace)
          "help" (open-help args is-show-project is-show-brick is-show-workspace is-fake-poly color-mode)
          "info" (info/info workspace unnamed-args)
          "libs" (libs workspace is-update)
          "overview" (overview/print-table workspace)
          "shell" (shell/start execute user-input workspace-fn workspace color-mode)
          "test" (test/run workspace unnamed-args test-result is-verbose color-mode)
          "version" (version)
          "ws" (ws-explorer/ws workspace get out color-mode)
          (unknown-command cmd))
        (println message))
      (exit-code/code cmd workspace @test-result))))
