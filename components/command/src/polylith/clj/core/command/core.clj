(ns polylith.clj.core.command.core
  (:require [clojure.pprint :as pp]
            [polylith.clj.core.command.deps-args :as deps-args]
            [polylith.clj.core.command.exit-code :as exit-code]
            [polylith.clj.core.command.test-args :as test-args]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.create.interfc :as create]
            [polylith.clj.core.deps.interfc :as deps]
            [polylith.clj.core.help.interfc :as help]
            [polylith.clj.core.user-config.interfc :as user-config]
            [polylith.clj.core.test-runner.interfc :as test-runner]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.workspace.interfc :as ws])
 (:refer-clojure :exclude [test]))

(defn check [{:keys [messages] :as workspace}]
  (let [color-mode (user-config/color-mode)]
    (if (empty? messages)
      (println (color/ok color-mode "OK"))
      (println (common/pretty-messages workspace)))))

(defn create-environment [ws-dir workspace env]
  (let [color-mode (-> workspace :settings :color-mode)]
    (when (= :ok (create/create-environment ws-dir workspace env))
      (create/print-alias-message env color-mode))))

(defn deps [workspace environment-name brick-name]
  (let [color-mode (-> workspace :settings :color-mode)]
    (if (deps-args/specified? environment-name)
      (if (deps-args/specified? brick-name)
        (deps/print-brick-table workspace environment-name brick-name color-mode)
        (deps/print-workspace-brick-table workspace environment-name color-mode))
      (if (deps-args/specified? brick-name)
        (deps/print-brick-ifc-table workspace brick-name color-mode)
        (deps/print-workspace-ifc-table workspace color-mode)))))

(defn diff [workspace]
  (doseq [file (-> workspace :changes :changed-files)]
    (println file)))

(defn help [workspace cmd]
  (let [color-mode (or (-> workspace :settings :color-mode) color/none)]
    (help/print-help cmd color-mode)))

(defn info [workspace arg]
  (let [show-loc? (= "-loc" arg)]
    (ws/print-table workspace show-loc?)))

(defn test [workspace arg1 arg2]
  (let [{:keys [env run-all? run-env-tests?]} (test-args/args arg1 arg2)]
    (test-runner/run workspace env run-all? run-env-tests?)))

(defn valid-command? [workspace cmd]
  (or (-> workspace nil? not)
      (nil? cmd)
      (= "help" cmd)
      (= "create-ws" cmd)))

(defn execute [current-dir workspace cmd arg1 arg2]
  (try
    (if (valid-command? workspace cmd)
      (case cmd
        "check" (check workspace)
        "create-ws" (create/create-workspace current-dir arg1 arg2)
        "create-env" (create-environment current-dir workspace arg1)
        "create-base" (create/create-base current-dir workspace arg1)
        "create-comp" (create/create-component current-dir workspace arg1 arg2)
        "deps" (deps workspace arg1 arg2)
        "diff" (diff workspace)
        "help" (help workspace arg1)
        "info" (info workspace arg1)
        "test" (test workspace arg1 arg2)
        "ws" (pp/pprint workspace)
        (help workspace nil))
      (println "  The command can only be executed from the workspace root."))
    {:exit-code (exit-code/code cmd workspace)}
    (catch Exception e
      {:exit-code 1
       :exception e})))
