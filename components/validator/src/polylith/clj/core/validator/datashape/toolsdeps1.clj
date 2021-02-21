(ns polylith.clj.core.validator.datashape.toolsdeps1
  (:require [malli.core :as m]
            [malli.error :as me]
            [polylith.clj.core.validator.datashape.shared :as shared]))

(defn validate-dev-config [config]
  (-> [:map
       [:polylith
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
          [:map-of symbol? symbol?]]]]
       [:aliases
        [:map
         [:dev shared/alias]
         [:test shared/alias]]]]
      (m/explain config)
      (me/humanize)))

(defn validate-project-deployable-config [config]
  (-> [:map
       [:paths
        [:vector string?]]
       [:deps {:optional true} [:map-of symbol? :map]]
       [:aliases
        [:map
         [:test shared/alias]]]]
      (m/explain config)
      (me/humanize)))
