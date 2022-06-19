(ns polylith.clj.core.command.core
  (:require [polylith.clj.core.command.cmd-validator.core :as cmd-validator]
            [polylith.clj.core.command.create :as create]
            [polylith.clj.core.command.dependencies :as dependencies]
            [polylith.clj.core.command.exit-code :as exit-code]
            [polylith.clj.core.command.info :as info]
            [polylith.clj.core.command.test :as test]
            [polylith.clj.core.tap.interface :as tap]
            [polylith.clj.core.command.user-config :as user-config]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.common.interface.config :as config]
            [polylith.clj.core.help.interface :as help]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.migrator.interface :as migrator]
            [polylith.clj.core.shell.interface :as shell]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.version.interface :as ver]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.ws-file.interface :as ws-file]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer])
  (:refer-clojure :exclude [test]))

(defn check [{:keys [messages] :as workspace} color-mode]
  (if (empty? messages)
    (println (color/ok color-mode "OK"))
    (validator/print-messages workspace)))

(defn diff [workspace]
  (doseq [file (-> workspace :changes :changed-files)]
    (println file)))

(defn help [[_ cmd ent] is-all is-show-project is-show-brick is-show-workspace toolsdeps1? color-mode]
  (help/print-help cmd ent is-all is-show-project is-show-brick is-show-workspace toolsdeps1? color-mode))

(defn version []
  (println (str "  " ver/name " (" ver/date ")")))

(defn unknown-command [cmd]
  (println (str "  Unknown command '" cmd "'. Type 'poly help' for help.")))

(defn prompt-message []
  (println "  Please use the 'shell' command instead, which gives you support for history (<up> key) and autocomplete (<tab> key)."))

(defn read-workspace
  ([ws-dir {:keys [ws-file] :as user-input}]
   (read-workspace ws-file ws-dir user-input (common/color-mode user-input)))
  ([ws-file ws-dir user-input color-mode]
   (if ws-file
     (ws-file/read-ws-from-file ws-file user-input)
     (when (config/valid-ws-root-config-file-found? ws-dir color-mode)
       (-> user-input
           ws-clj/workspace-from-disk
           ws/enrich-workspace
           change/with-changes)))))

(defn workspace-reader-fn [color-mode]
  (fn [user-input ws-file ws-dir]
    (read-workspace ws-file ws-dir user-input color-mode)))

(defn execute [{:keys [cmd args name top-ns branch is-tap is-git-add is-commit is-all is-show-brick is-show-workspace is-show-project is-verbose get out interface selected-bricks selected-projects unnamed-args ws-file] :as user-input}]
  (let [color-mode (common/color-mode user-input)
        ws-dir (common/workspace-dir user-input color-mode)
        workspace-fn (workspace-reader-fn color-mode)
        workspace (workspace-fn user-input ws-file ws-dir)]
    (user-config/create-user-config-if-not-exists)
    (when is-tap (tap/execute "open"))
    (let [brick-name (first selected-bricks)
          project-name (first selected-projects)
          toolsdeps1? (common/toolsdeps1? workspace)
          test-result (atom true)
          [ok? message] (cmd-validator/validate workspace user-input color-mode)]
      (if ok?
        (case cmd
          nil (shell/start execute user-input workspace-fn workspace color-mode)
          "check" (check workspace color-mode)
          "create" (create/create ws-dir workspace args name top-ns interface branch is-git-add is-commit color-mode)
          "deps" (dependencies/deps workspace project-name brick-name unnamed-args is-all)
          "diff" (diff workspace)
          "help" (help args is-all is-show-project is-show-brick is-show-workspace toolsdeps1? color-mode)
          "info" (info/info workspace unnamed-args)
          "libs" (lib/print-lib-table workspace is-all)
          "migrate" (migrator/migrate ws-dir workspace)
          "prompt" (prompt-message)
          "shell" (shell/start execute user-input workspace-fn workspace color-mode)
          "test" (test/run user-input workspace unnamed-args test-result is-verbose color-mode)
          "version" (version)
          "ws" (ws-explorer/ws workspace get out color-mode)
          (unknown-command cmd))
        (println message))
      (exit-code/code cmd workspace @test-result))))
