(ns polylith.clj.core.help.interfc
  (:require [polylith.clj.core.help.main :as main]))

(defn print-help [color-mode]
  (main/print-help color-mode))
