(ns polylith.clj.core.command.core
  (:require [clojure.pprint :as pp]
            [polylith.clj.core.command.create :as create]
            [polylith.clj.core.command.deps :as deps]
            [polylith.clj.core.command.exit-code :as exit-code]
            [polylith.clj.core.command.info :as info]
            [polylith.clj.core.command.message :as message]
            [polylith.clj.core.command.test :as test]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.help.interface :as help]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.workspace.interface :as ws])
  (:refer-clojure :exclude [test]))

(defn check [{:keys [messages] :as workspace} color-mode]
  (if (empty? messages)
    (println (color/ok color-mode "OK"))
    (println (common/pretty-messages workspace))))

(defn diff [workspace]
  (doseq [file (-> workspace :changes :changed-files)]
    (println file)))

(defn help [cmd color-mode]
  (help/print-help cmd color-mode))

(defn unknown-command [cmd]
  (println (str "  Unknown command '" cmd "'. Type 'help' for help.")))

(defn can-be-executed-from-here? [workspace cmd]
  (or (-> workspace nil? not)
      (nil? cmd)
      (= "help" cmd)
      (= "create" cmd)))

(defn validate [{:keys [settings environments] :as workspace} cmd active-dev-profiles selected-environments color-mode]
  (if (can-be-executed-from-here? workspace cmd)
    (validator/validate active-dev-profiles selected-environments settings environments color-mode)
    [false message/cant-be-executed-outside-ws-message]))

(defn valid-config-file? [ws-dir color-mode]
  (try
    (and (file/exists (str ws-dir "/deps.edn"))
         (:polylith (read-string (slurp (str ws-dir "/deps.edn")))))
    (catch Exception e
      (println (str (color/error color-mode "  Error: ") "couldn't read deps.edn: " (.getMessage e))))))

(defn read-workspace [ws-dir user-input color-mode]
  (when (valid-config-file? ws-dir color-mode)
    (-> user-input
        ws-clj/workspace-from-disk
        ws/enrich-workspace
        change/with-changes)))

(defn execute [{:keys [cmd arg1 name top-ns brick interface show-lib? active-dev-profiles selected-environments unnamed-args] :as user-input}]
  (try
    (let [ws-dir (common/workspace-dir user-input)
          color-mode (common/color-mode user-input)
          environment-name (first selected-environments)
          workspace (read-workspace ws-dir user-input color-mode)
          [ok? message] (validate workspace cmd active-dev-profiles selected-environments color-mode)]
      (if ok?
        (case cmd
          "check" (check workspace color-mode)
          "create" (create/create ws-dir workspace arg1 name top-ns interface color-mode)
          "deps" (deps/deps workspace environment-name brick unnamed-args show-lib?)
          "diff" (diff workspace)
          "help" (help arg1 color-mode)
          "info" (info/info workspace unnamed-args)
          "test" (test/run workspace unnamed-args)
          "ws" (pp/pprint workspace)
          (unknown-command cmd))
        (println message))
      {:exit-code (exit-code/code cmd workspace)})
    (catch Exception e
      {:exit-code 1
       :exception e})))
