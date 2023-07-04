(ns polylith.clj.core.help.core
  (:require [polylith.clj.core.help.check :as check]
            [polylith.clj.core.help.create :as create]
            [polylith.clj.core.help.deps :as deps]
            [polylith.clj.core.help.diff :as diff]
            [polylith.clj.core.help.info :as info]
            [polylith.clj.core.help.libs :as libs]
            [polylith.clj.core.help.switch-ws :as switch-ws]
            [polylith.clj.core.help.migrate :as migrate]
            [polylith.clj.core.help.overview :as overview]
            [polylith.clj.core.help.shell :as shell]
            [polylith.clj.core.help.tap :as tap]
            [polylith.clj.core.help.test :as test]
            [polylith.clj.core.help.version :as version]
            [polylith.clj.core.help.ws :as ws]
            [polylith.clj.core.help.summary :as summary]))

(defn print-help [cmd ent is-all is-show-project is-show-brick is-show-workspace toolsdeps1? color-mode]
  (case cmd
    "check" (check/print-help color-mode)
    "create" (create/print-help ent color-mode)
    "deps" (deps/print-help is-show-project is-show-brick is-show-workspace color-mode)
    "diff" (diff/print-help color-mode)
    "info" (info/print-help color-mode)
    "libs" (libs/print-help color-mode)
    "overview" (overview/print-help color-mode)
    "switch-ws" (switch-ws/print-help color-mode)
    "migrate" (migrate/print-help)
    "shell" (shell/print-help color-mode)
    "tap" (tap/print-help color-mode)
    "test" (test/print-help color-mode)
    "version" (version/print-help)
    "ws" (ws/print-help color-mode)
    (summary/print-help is-all toolsdeps1? color-mode)))
