(ns polylith.clj.core.help.create
  (:require [polylith.clj.core.help.create-component :as component]
            [polylith.clj.core.help.create-base :as base]
            [polylith.clj.core.help.create-environment :as environment]
            [polylith.clj.core.help.create-workspace :as workspace]))

(defn help-text [])

(defn print-help [ent color-mode]
  (case ent
    "c" (component/print-help color-mode)
    "b" (base/print-help color-mode)
    "e" (environment/print-help color-mode)
    "w" (workspace/print-help color-mode)))
