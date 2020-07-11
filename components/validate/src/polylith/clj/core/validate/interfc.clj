(ns polylith.clj.core.validate.interfc
  (:require [polylith.clj.core.validate.core :as core]))

(defn messages [top-ns interface-names interfaces components bases environments color-mode]
  (core/messages top-ns interface-names interfaces components bases environments color-mode))
