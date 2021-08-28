(ns polylith.clj.core.command.core
  (:require [polylith.clj.core.command.cmd-validator.core :as cmd-validator]
            [polylith.clj.core.command.create :as create]
            [polylith.clj.core.command.dependencies :as dependencies]
            [polylith.clj.core.command.exit-code :as exit-code]
            [polylith.clj.core.command.info :as info]
            [polylith.clj.core.command.prompt :as prompt]
            [polylith.clj.core.command.test :as test]
            [polylith.clj.core.command.user-config :as user-config]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.migrator.interface :as migrator]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.help.interface :as help]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.util.interface.color :as color]
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

(defn help [prompt? [_ cmd ent] is-show-project is-show-brick is-show-workspace toolsdeps1? color-mode]
  (help/print-help prompt? cmd ent is-show-project is-show-brick is-show-workspace toolsdeps1? color-mode))

(defn version []
  (println (str "  " ver/name " (" ver/date ")")))

(defn unknown-command [cmd]
  (println (str "  Unknown command '" cmd "'. Type 'poly help' for help.")))

(defn read-workspace
  ([ws-dir {:keys [ws-file] :as user-input}]
   (read-workspace ws-file ws-dir user-input (common/color-mode user-input)))
  ([ws-file ws-dir user-input color-mode]
   (if ws-file
     (ws-file/read-ws-from-file ws-file user-input)
     (when (common/valid-ws-root-config-file-found? ws-dir color-mode)
       (-> user-input
           ws-clj/workspace-from-disk
           ws/enrich-workspace
           change/with-changes)))))

(defn execute [{:keys [cmd args name top-ns branch is-git-add is-prompt ws-file is-all is-show-brick is-show-workspace is-show-project is-verbose get out interface selected-bricks selected-projects unnamed-args] :as user-input}]
  (user-config/create-user-config-if-not-exists)
  (let [color-mode (common/color-mode user-input)
        ws-dir (common/workspace-dir user-input color-mode)
        brick-name (first selected-bricks)
        project-name (first selected-projects)
        workspace (read-workspace ws-file ws-dir user-input color-mode)
        toolsdeps1? (common/toolsdeps1? workspace)
        [ok? message] (cmd-validator/validate workspace user-input color-mode)]
    (if ok?
      (case cmd
        "check" (check workspace color-mode)
        "create" (create/create ws-dir workspace args name top-ns interface branch is-git-add color-mode)
        "deps" (dependencies/deps workspace project-name brick-name unnamed-args is-all)
        "diff" (diff workspace)
        "help" (help is-prompt args is-show-project is-show-brick is-show-workspace toolsdeps1? color-mode)
        "info" (info/info workspace unnamed-args)
        "libs" (lib/print-lib-table workspace is-all)
        "migrate" (migrator/migrate ws-dir workspace)
        "prompt" (prompt/start-user-prompt execute workspace color-mode)
        "test" (test/run workspace unnamed-args is-verbose color-mode)
        "version" (version)
        "ws" (ws-explorer/ws workspace get out color-mode)
        (unknown-command cmd))
      (println message))
    (exit-code/code cmd workspace)))
