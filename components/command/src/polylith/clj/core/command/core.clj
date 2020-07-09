(ns polylith.clj.core.command.core
  (:require [polylith.clj.core.command.cmd.check :as check]
            [polylith.clj.core.command.cmd.info :as info]
            [polylith.clj.core.help.interfc :as help]
            [polylith.clj.core.test-runner.interfc :as test-runner]))

(defn execute-command [workspace cmd arg]
  (let [color-mode (-> workspace :settings :color-mode)]
    (case cmd
      "check" (check/execute workspace)
      "help" (help/print-help color-mode)
      "info" (info/execute workspace arg)
      "test" (test-runner/run workspace arg)
      (help/print-help color-mode))))
