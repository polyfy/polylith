(ns polylith.clj.core.command.core
  (:require [clojure.pprint :as pp]
            [polylith.clj.core.command.create :as create]
            [polylith.clj.core.command.deps :as deps]
            [polylith.clj.core.command.exit-code :as exit-code]
            [polylith.clj.core.command.info :as info]
            [polylith.clj.core.command.message :as message]
            [polylith.clj.core.command.test-args :as test-args]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.help.interfc :as help]
            [polylith.clj.core.user-config.interfc :as user-config]
            [polylith.clj.core.test-runner.interfc :as test-runner]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.util.interfc.params :as params]
            [polylith.clj.core.workspace.interfc :as ws])
 (:refer-clojure :exclude [test]))

(defn check [{:keys [messages] :as workspace}]
  (let [color-mode (user-config/color-mode)]
    (if (empty? messages)
      (println (color/ok color-mode "OK"))
      (println (common/pretty-messages workspace)))))

(defn diff [workspace]
  (doseq [file (-> workspace :changes :changed-files)]
    (println file)))

(defn help [workspace cmd]
  (let [color-mode (or (-> workspace :settings :color-mode) color/none)]
    (help/print-help cmd color-mode)))

(defn test [workspace arg1 arg2]
  (let [{:keys [env run-all? run-env-tests?]} (test-args/args arg1 arg2)]
    (test-runner/run workspace env run-all? run-env-tests?)))

(defn can-be-executed-from-here? [workspace cmd]
  (or (-> workspace nil? not)
      (nil? cmd)
      (= "help" cmd)
      (= "create" cmd)))

(defn execute [current-dir workspace cmd arg1 arg2 arg3]
  (try
    (if (can-be-executed-from-here? workspace cmd)
      (let [color-mode (-> workspace :settings :color-mode)
            {:keys [named-args unnamed-args]} (params/parse arg1 arg2 arg3)
            {:keys [name top-ns env brick interface loc]} named-args]
        (case cmd
          "check" (check workspace)
          "create" (create/create current-dir workspace arg1 name top-ns interface color-mode)
          "deps" (deps/deps workspace env brick unnamed-args color-mode)
          "diff" (diff workspace)
          "help" (help workspace arg1)
          "info" (info/info workspace loc unnamed-args)
          "test" (test workspace arg1 arg2)
          "ws" (pp/pprint workspace)
          (help workspace nil)))
      (message/print-cant-be-executed-outside-ws))
    {:exit-code (exit-code/code cmd workspace)}
    (catch Exception e
      {:exit-code 1
       :exception e})))
