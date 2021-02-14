(ns polylith.clj.core.validator.datashape.toolsdeps2
  (:require [malli.core :as m]
            [malli.error :as me]
            [polylith.clj.core.validator.datashape.shared :as shared]))

(defn validate-brick-config [config]
  (-> [:map
       [:paths [:vector string?]]
       [:deps [:map-of symbol? map?]]
       [:aliases {:optional true} [:map]]]
      (m/explain config)
      (me/humanize)))

(defn validate-dev-config [config]
  (-> [:map
       [:aliases
        [:map
         [:dev shared/alias]
         [:test shared/alias]]]]
      (m/explain config)
      (me/humanize)))

(defn validate-workspace-config [config]
  (-> [:map
       [:vcs {:optional true} string?]
       [:top-namespace string?]
       [:interface-ns {:optional true} string?]
       [:default-profile-name {:optional true} string?]
       [:compact-views {:optional true} set?]
       [:release-tag-pattern {:optional true} string?]
       [:stable-tag-pattern {:optional true} string?]
       [:project-to-alias {:optional true}
        [:map-of :string :string]]
       [:input-type keyword?]]
      (m/explain config)
      (me/humanize)))
