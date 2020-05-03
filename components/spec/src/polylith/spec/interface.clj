(ns polylith.spec.interface
  (:require [polylith.spec.core :as core]))

(defn valid-config? [config]
  (core/valid-config? config))
