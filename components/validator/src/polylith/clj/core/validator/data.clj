(ns polylith.clj.core.validator.data
  (:require [malli.core :as m]
            [malli.error :as me])
  (:refer-clojure :exclude [alias]))

(def alias [:map
            [:extra-paths
             [:vector string?]]
            [:extra-deps {:optional true} [:map-of symbol? :map]]])

(def polylith [:polylith
               [:map
                [:top-namespace string?]
                [:interface-ns string?]
                [:default-profile-name string?]
                [:compact-views set?]
                [:release-tag-pattern string?]
                [:stable-tag-pattern string?]
                [:env-to-alias {:optional true}
                 [:map-of :string :string]]
                [:ns-to-lib
                 [:map-of symbol? symbol?]]]])

(defn validate-dev-config [config]
  (-> [:map
       polylith
       [:aliases
        [:map
         [:dev alias]
         [:test alias]]]]
      (m/explain config)
      (me/humanize)))

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
