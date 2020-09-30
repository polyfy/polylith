(ns polylith.clj.core.help.interface
  (:require [polylith.clj.core.help.core :as core]))

(defn print-help [cmd ent is-show-env show-brick? show-bricks? color-mode]
  (core/print-help cmd ent is-show-env show-brick? show-bricks? color-mode))
