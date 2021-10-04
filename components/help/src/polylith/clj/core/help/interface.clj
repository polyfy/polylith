(ns polylith.clj.core.help.interface
  (:require [polylith.clj.core.help.core :as core]))

(defn print-help [cmd ent is-all is-show-project is-show-brick is-show-workspace toolsdeps1? color-mode]
  (core/print-help cmd ent is-all is-show-project is-show-brick is-show-workspace toolsdeps1? color-mode))
