(ns polylith.clj.core.func-api.interfc
  (:require [polylith.clj.core.func-api.core :as core]))

(defn changed-environments []
  (core/changed-environments))
