(ns ^:no-doc polylith.clj.core.user-input.interface
  (:require [polylith.clj.core.user-input.core :as core]))

(defn extract-arguments [args]
  (core/extract-arguments args #{"ws-dir"}))
