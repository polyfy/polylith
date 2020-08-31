(ns polylith.clj.core.api.interfc
  (:require [polylith.clj.core.api.core :as core]))

(defn changed-environments []
  (core/changed-environments))
