(ns ^:no-doc polylith.clj.core.user-input.interface
  (:require [polylith.clj.core.user-input.core :as core]))

(defn extract-params [args]
  (core/extract-params args #{"ws-dir"}))
