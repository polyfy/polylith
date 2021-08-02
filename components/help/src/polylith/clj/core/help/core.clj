(ns polylith.clj.core.help.core
  (:require [polylith.clj.core.help.check :as check]
            [polylith.clj.core.help.create :as create]
            [polylith.clj.core.help.deps :as deps]
            [polylith.clj.core.help.diff :as diff]
            [polylith.clj.core.help.info :as info]
            [polylith.clj.core.help.libs :as libs]
            [polylith.clj.core.help.migrate :as migrate]
            [polylith.clj.core.help.prompt :as prompt]
            [polylith.clj.core.help.test :as test]
            [polylith.clj.core.help.ws :as ws]
            [polylith.clj.core.help.summary :as summary]))

(defn print-help [prompt? cmd ent is-show-project is-show-brick is-show-workspace toolsdeps1? color-mode]
  (case cmd
    "check" (check/print-help color-mode)
    "create" (create/print-help ent color-mode)
    "deps" (deps/print-help is-show-project is-show-brick is-show-workspace color-mode)
    "diff" (diff/print-help color-mode)
    "info" (info/print-help color-mode)
    "libs" (libs/print-help color-mode)
    "migrate" (migrate/print-help)
    "prompt" (prompt/print-help)
    "test" (test/print-help color-mode)
    "ws" (ws/print-help color-mode)
    (summary/print-help prompt? toolsdeps1? color-mode)))
