(ns polylith.clj.core.validator.datashape.shared
  (:require [malli.core :as m]
            [malli.error :as me])
  (:refer-clojure :exclude [alias]))

(def alias [:map
            [:extra-paths
             [:vector string?]]
            [:extra-deps {:optional true} [:map-of symbol? :map]]])

(defn validate-deployable-config [config]
  (-> [:map
       [:paths
        [:vector string?]]
       [:deps {:optional true} [:map-of symbol? :map]]
       [:aliases
        [:map
         [:test alias]]]]
      (m/explain config)
      (me/humanize)))
