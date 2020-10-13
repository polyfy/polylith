(ns polylith.clj.core.validator.data
  (:require [malli.core :as m]
            [malli.error :as me])
  (:refer-clojure :exclude [alias]))


(def alias [:map
            [:extra-paths vector?]
            [:extra-deps {:optional true} [:map-of symbol? :map]]])

(defn validate-deps-edn [config]
  (-> [:map
       [:polylith
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
          [:map-of symbol? symbol?]]]]
       [:aliases
        [:map
         [:dev alias]
         [:test alias]]]]
      (m/explain config)
      (me/humanize)))
