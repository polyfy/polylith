(ns polylith.clj.core.env-reloader.interfc
  (:require [polylith.clj.core.env-reloader.core :as core]))

(defn reload [env]
  (core/reload env))
