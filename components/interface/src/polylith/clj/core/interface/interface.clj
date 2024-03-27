(ns ^:no-doc polylith.clj.core.interface.interface
  [:require [polylith.clj.core.interface.core :as core]])

(defn calculate [components]
  (core/calculate components))
