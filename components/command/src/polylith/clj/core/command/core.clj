(ns polylith.clj.core.command.core
  (:require [clojure.pprint :as pp]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.deps.interfc :as deps]
            [polylith.clj.core.help.interfc :as help]
            [polylith.clj.core.test-runner.interfc :as test-runner]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.workspace.interfc :as ws]))

(defn check [{:keys [messages settings] :as workspace}]
  (let [color-mode (:color-mode settings color/none)]
    (if (empty? messages)
      (println (color/ok color-mode "OK"))
      (println (common/pretty-messages workspace)))))

(defn deps [workspace environment-name brick-name]
  (let [color-mode (-> workspace :settings :color-mode)]
    (if (and environment-name brick-name)
      (deps/print-brick-table workspace environment-name brick-name color-mode)
      (println (str "Both environment and brick name must be given, e.g.: "
                    (color/environment "myenv" color-mode) " mybrick")))))

(defn info [workspace arg]
  (case arg
    "-dump" (pp/pprint workspace)
    "-loc" (ws/print-table workspace true)
    (ws/print-table workspace false)))

(defn help [workspace cmd]
  (let [color-mode (-> workspace :settings :color-mode)]
    (help/print-help cmd color-mode)))

(defn test-ws [workspace arg1 arg2]
  (if (= arg1 "-all")
    (test-runner/run workspace arg2 true)
    (test-runner/run workspace arg1 (= "-all" arg2))))

(defn execute [workspace cmd arg1 arg2]
  (case cmd
    "check" (check workspace)
    "deps" (deps workspace arg1 arg2)
    "help" (help workspace arg1)
    "info" (info workspace arg1)
    "test" (test-ws workspace arg1 arg2)
    (help workspace nil)))
