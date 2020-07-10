(ns polylith.clj.core.command.core
  (:require [clojure.pprint :as pp]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.help.interfc :as help]
            [polylith.clj.core.test-runner.interfc :as test-runner]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.workspace.interfc :as ws]))

(defn check [{:keys [messages settings] :as workspace}]
  (let [color-mode (:color-mode settings color/none)]
    (if (empty? messages)
      (println (color/ok color-mode "OK"))
      (println (common/pretty-messages workspace)))))

(defn info [workspace arg]
  (case arg
    "-dump" (pp/pprint workspace)
    "-loc" (ws/print-table workspace true)
    (ws/print-table workspace false)))

(defn help [workspace cmd]
  (let [color-mode (-> workspace :settings :color-mode)]
    (help/print-help cmd color-mode)))

(defn execute [workspace cmd arg]
  (case cmd
    "check" (check workspace)
    "help" (help workspace arg)
    "info" (info workspace arg)
    "test" (test-runner/run workspace arg)
    (help workspace nil)))
