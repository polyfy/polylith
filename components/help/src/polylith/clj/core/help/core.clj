(ns polylith.clj.core.help.core
  (:require [polylith.clj.core.help.check :as check]
            [polylith.clj.core.help.info :as info]
            [polylith.clj.core.help.test :as test]
            [polylith.clj.core.help.summary :as summary]))

(defn print-help [cmd color-mode]
  (case cmd
    "check" (check/print-help color-mode)
    "info" (info/print-help color-mode)
    "test" (test/print-help color-mode)
    (summary/print-help color-mode)))
