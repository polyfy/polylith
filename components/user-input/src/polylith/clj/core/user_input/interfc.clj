(ns polylith.clj.core.user-input.interfc
  (:require [polylith.clj.core.user-input.core :as core]))

(defn extract-params [args]
  (core/extract-params args))
