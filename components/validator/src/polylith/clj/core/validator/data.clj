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
                [:vcs {:optional true} string?]
                [:top-namespace string?]
                [:interface-ns {:optional true} string?]
                [:default-profile-name {:optional true} string?]
                [:compact-views {:optional true} set?]
                [:release-tag-pattern {:optional true} string?]
                [:stable-tag-pattern {:optional true} string?]
                [:project-to-alias {:optional true}
                 [:map-of :string :string]]
                [:ns-to-lib {:optional true}
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
