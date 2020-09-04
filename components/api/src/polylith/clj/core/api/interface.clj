(ns polylith.clj.core.api.interface
  (:require [polylith.clj.core.api.core :as core]))

(defn changed-environments []
  (core/changed-environments))
