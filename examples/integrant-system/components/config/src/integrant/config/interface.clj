(ns
  ^{:doc    "An abstract system configuration as a Polylith component"
    :author "Mark Sto"}
  integrant.config.interface
  (:require [integrant.config.core :as core]))

(defn load-config
  []
  (core/load-config))
