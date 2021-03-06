(ns polylith.clj.core.command.core
  (:require [polylith.clj.core.command.cmd-validator.core :as cmd-validator]
            [polylith.clj.core.command.create :as create]
            [polylith.clj.core.command.dependencies :as dependencies]
            [polylith.clj.core.command.exit-code :as exit-code]
            [polylith.clj.core.command.info :as info]
            [polylith.clj.core.command.test :as test]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.help.interface :as help]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.version.interface :as ver]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer])
  (:refer-clojure :exclude [test]))

(defn check [{:keys [messages] :as workspace} color-mode]
  (if (empty? messages)
    (println (color/ok color-mode "OK"))
    (validator/print-messages workspace)))

(defn diff [workspace]
  (doseq [file (-> workspace :changes :changed-files)]
    (println file)))

(defn help [[_ cmd ent] is-show-project is-show-brick is-show-bricks color-mode]
  (help/print-help cmd ent is-show-project is-show-brick is-show-bricks color-mode))

(defn version []
  (println (str "  " ver/version " (" ver/date ")")))

(defn unknown-command [cmd]
  (println (str "  Unknown command '" cmd "'. Type 'poly help' for help.")))

(defn read-ws-from-file [ws-file {:keys [ selected-profiles] :as user-input}]
  (if (not (file/exists ws-file))
    (println (str "The file '" ws-file "' doesn't exist."))
    (let [ws (first (file/read-file ws-file))
          old-user-input (-> ws :user-input)
          new-ws (-> (assoc ws :old-user-input old-user-input)
                     (assoc :user-input user-input))]
      (if (empty? selected-profiles)
        new-ws
        (assoc-in new-ws [:settings :active-profiles] selected-profiles)))))

(defn read-workspace
  ([ws-dir {:keys [ws-file] :as user-input}]
   (read-workspace ws-file ws-dir user-input (common/color-mode user-input)))
  ([ws-file ws-dir user-input color-mode]
   (if (nil? ws-file)
     (when (common/valid-ws-root-config-file-found? ws-dir color-mode)
       (-> user-input
           ws-clj/workspace-from-disk
           ws/enrich-workspace
           change/with-changes))
     (read-ws-from-file ws-file user-input))))

(defn execute [{:keys [cmd args name top-ns ws-file is-tap is-all is-show-brick is-show-bricks is-show-project brick get out interface selected-projects unnamed-args] :as user-input}]
  (let [color-mode (common/color-mode user-input)
        ws-dir (common/workspace-dir user-input color-mode)
        project-name (first selected-projects)
        workspace (read-workspace ws-file ws-dir user-input color-mode)
        [ok? message] (cmd-validator/validate workspace user-input color-mode)]
    (if ok?
      (case cmd
        "check" (check workspace color-mode)
        "create" (create/create ws-dir workspace args name top-ns interface color-mode)
        "deps" (dependencies/deps workspace project-name brick unnamed-args)
        "diff" (diff workspace)
        "help" (help args is-show-project is-show-brick is-show-bricks color-mode)
        "info" (info/info workspace unnamed-args)
        "libs" (lib/print-lib-table workspace is-all)
        "test" (test/run workspace unnamed-args color-mode)
        "version" (version)
        "ws" (ws-explorer/ws workspace get out color-mode)
        (unknown-command cmd))
      (println message))
    (exit-code/code cmd workspace)))
