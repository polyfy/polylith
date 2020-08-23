(ns polylith.clj.core.command.core
  (:require [clojure.pprint :as pp]
            [polylith.clj.core.command.create :as create]
            [polylith.clj.core.command.deps :as deps]
            [polylith.clj.core.command.exit-code :as exit-code]
            [polylith.clj.core.command.info :as info]
            [polylith.clj.core.command.message :as message]
            [polylith.clj.core.command.test :as test]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.validator.interfc :as validator]
            [polylith.clj.core.help.interfc :as help]
            [polylith.clj.core.user-config.interfc :as user-config]
            [polylith.clj.core.util.interfc.color :as color])
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

(defn execute [current-dir workspace {:keys [cmd arg1 name top-ns env brick interface show-lib? active-dev-profiles selected-environments unnamed-args]}]
  "We need to pass in user-info separately, because when the 'create w' command is executed
   we don't have a workspace yet."
  (try
    (let [color-mode (user-config/color-mode)
          [ok? message] (validate workspace cmd active-dev-profiles selected-environments color-mode)]
      (if ok?
        (case cmd
          "check" (check workspace color-mode)
          "create" (create/create current-dir workspace arg1 name top-ns interface color-mode)
          "deps" (deps/deps workspace env brick unnamed-args show-lib?)
          "diff" (diff workspace)
          "help" (help arg1 color-mode)
          "info" (info/info workspace unnamed-args)
          "test" (test/run workspace unnamed-args)
          "ws" (pp/pprint workspace)
          (unknown-command cmd))
        (println message)))
    {:exit-code (exit-code/code cmd workspace)}
    (catch Exception e
      {:exit-code 1
       :exception e})))
