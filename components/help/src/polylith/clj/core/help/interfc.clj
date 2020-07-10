(ns polylith.clj.core.help.interfc
  (:require [polylith.clj.core.help.core :as core]))

(defn print-help [cmd color-mode]
  (core/print-help cmd color-mode))
