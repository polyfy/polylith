(ns polylith.clj.core.command.core
  (:require [polylith.clj.core.command.create :as create]
            [polylith.clj.core.command.dependencies :as dependencies]
            [polylith.clj.core.command.exit-code :as exit-code]
            [polylith.clj.core.command.info :as info]
            [polylith.clj.core.command.message :as message]
            [polylith.clj.core.command.test :as test]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.help.interface :as help]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.util.interface.color :as color]
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

(defn help [cmd ent is-show-env is-show-brick is-show-bricks color-mode]
  (help/print-help cmd ent is-show-env is-show-brick is-show-bricks color-mode))

(defn unknown-command [cmd]
  (println (str "  Unknown command '" cmd "'. Type 'poly help' for help.")))

(defn can-be-executed-from-here? [workspace cmd]
  (or (-> workspace nil? not)
      (nil? cmd)
      (= "help" cmd)
      (= "create" cmd)))

(defn validate [{:keys [settings environments] :as workspace} cmd active-dev-profiles selected-environments color-mode]
  (if (can-be-executed-from-here? workspace cmd)
    (validator/validate active-dev-profiles selected-environments settings environments color-mode)
    [false (message/cant-be-executed-outside-ws-message cmd)]))

(defn read-ws-from-file [cmd ws-file user-input]
  (if (contains? #{"create" "test"} cmd)
    (println (str "  The '" cmd "' command can't be executed when the workspace is read from file via 'ws-file'."))
    (if (not (file/exists ws-file))
      (println (str "The file '" ws-file "' doesn't exist."))
      (let [ws (first (file/read-file ws-file))
            old-user-input (-> ws :settings :user-input)]
        (-> (assoc ws :user-input-old old-user-input)
            (assoc :user-input user-input))))))

(defn read-workspace
  ([ws-dir {:keys [cmd ws-file] :as user-input}]
   (read-workspace cmd ws-file ws-dir user-input (common/color-mode user-input)))
  ([cmd ws-file ws-dir user-input color-mode]
   (if (nil? ws-file)
     (when (common/valid-config-file? ws-dir color-mode)
       (-> user-input
           ws-clj/workspace-from-disk
           ws/enrich-workspace
           change/with-changes))
     (read-ws-from-file cmd ws-file user-input))))

(defn execute [{:keys [cmd args name top-ns ws-file is-show-brick is-show-bricks is-show-env brick get out interface active-dev-profiles selected-environments unnamed-args] :as user-input}]
  (let [color-mode (common/color-mode user-input)
        ws-dir (common/workspace-dir user-input color-mode)
        environment-name (first selected-environments)
        workspace (read-workspace cmd ws-file ws-dir user-input color-mode)
        arg1 (second args)
        arg2 (-> args rest second)
        [ok? message] (validate workspace cmd active-dev-profiles selected-environments color-mode)]
    (if ok?
      (case cmd
        "check" (check workspace color-mode)
        "create" (create/create ws-dir workspace arg1 name top-ns interface color-mode)
        "deps" (dependencies/deps workspace environment-name brick unnamed-args)
        "diff" (diff workspace)
        "help" (help arg1 arg2 is-show-env is-show-brick is-show-bricks color-mode)
        "info" (info/info workspace unnamed-args)
        "libs" (deps/print-lib-table workspace)
        "test" (test/run workspace unnamed-args)
        "ws" (ws-explorer/ws workspace get out color-mode)
        (unknown-command cmd))
      (println message))
    (exit-code/code cmd workspace)))
