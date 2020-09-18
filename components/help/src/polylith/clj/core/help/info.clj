(ns polylith.clj.core.help.info
  (:require [polylith.clj.core.help.shared :as shared]
            [polylith.clj.core.util.interface.color :as color]))

(defn help-text [color-mode])

(defn print-help [color-mode]
  (println (help-text color-mode)))
