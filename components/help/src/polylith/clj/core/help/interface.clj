(ns polylith.clj.core.help.interface
  (:require [polylith.clj.core.help.core :as core]))

(defn print-help [cmd ent is-show-project is-show-brick is-show-bricks color-mode]
  (core/print-help cmd ent is-show-project is-show-brick is-show-bricks color-mode))
