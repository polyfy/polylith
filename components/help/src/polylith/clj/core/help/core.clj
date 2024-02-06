(ns ^:no-doc polylith.clj.core.help.core
  (:require [polylith.clj.core.help.check :as check]
            [polylith.clj.core.help.create :as create]
            [polylith.clj.core.help.deps :as deps]
            [polylith.clj.core.help.diff :as diff]
            [polylith.clj.core.help.doc :as doc]
            [polylith.clj.core.help.info :as info]
            [polylith.clj.core.help.libs :as libs]
            [polylith.clj.core.help.switch-ws :as switch-ws]
            [polylith.clj.core.help.overview :as overview]
            [polylith.clj.core.help.shell :as shell]
            [polylith.clj.core.help.tap :as tap]
            [polylith.clj.core.help.test :as test]
            [polylith.clj.core.help.version :as version]
            [polylith.clj.core.help.ws :as ws]
            [polylith.clj.core.help.summary :as summary]
            [polylith.clj.core.system.interface :as system]))

(defn print-help [cmd ent is-show-project is-show-brick is-show-workspace fake-poly? color-mode]
  (let [extended? (and system/extended?
                       (not fake-poly?))]
    (case cmd
      "check" (check/print-help color-mode)
      "create" (create/print-help ent color-mode)
      "deps" (deps/print-help is-show-project is-show-brick is-show-workspace extended? color-mode)
      "diff" (diff/print-help color-mode)
      "doc" (doc/print-help color-mode)
      "info" (info/print-help extended? color-mode)
      "libs" (libs/print-help extended? color-mode)
      "overview" (overview/print-help color-mode)
      "switch-ws" (switch-ws/print-help color-mode)
      "shell" (shell/print-help color-mode)
      "tap" (tap/print-help color-mode)
      "test" (test/print-help color-mode)
      "version" (version/print-help)
      "ws" (ws/print-help color-mode)
      (summary/print-help extended? fake-poly? color-mode))))
