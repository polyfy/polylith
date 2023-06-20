(ns polylith.clj.core.validator.datashape.toolsdeps2
  (:require [malli.core :as m]
            [malli.error :as me]
            [malli.util :as mu]
            [polylith.clj.core.validator.datashape.shared :as shared]))

(defn validate-brick-config [config filename]
  (-> [:map
       [:paths [:* [:alt keyword? string?]]]
       [:deps [:map-of symbol? map?]]
       [:aliases {:optional true} [:map]]]
      (m/explain config)
      (me/humanize)
      (shared/error-message filename)))

(defn validate-project-dev-config [config filename]
  (-> [:map
       [:aliases
        [:map
         [:dev shared/alias]
         [:test shared/alias]]]]
      (m/explain config)
      (me/humanize)
      (shared/error-message filename)))

(defn validate-project-deployable-config [config filename]
  (-> [:map
       [:paths {:optional true}
        [:* [:alt keyword? string?]]]
       [:deps {:optional true} [:map-of symbol? :map]]]
      (m/explain config)
      (me/humanize)
      (shared/error-message filename)))

(def test-runner-constructor-schema
  [:or qualified-symbol? [:enum :default]])

(def test-runner-config-schema
  [:map
   [:create-test-runner {:optional true}
    [:or test-runner-constructor-schema
     [:repeat {:min 1} test-runner-constructor-schema]]]])

(def project-test-config-schema
  (mu/merge
   test-runner-config-schema
   [:map
    [:include {:optional true} vector?]
    [:exclude {:optional true} vector?]
    [:setup-fn {:optional true} qualified-symbol?]
    [:teardown-fn {:optional true} qualified-symbol?]]))

(def project-settings-schema
  [:map
   [:alias {:optional true} string?]
   [:test {:optional true}
    [:or project-test-config-schema
     vector?]]]) ;; legacy

(def workspace-schema
  [:map
   [:vcs {:optional true} [:or string? map?]]
   [:top-namespace string?]
   [:interface-ns {:optional true} string?]
   [:default-profile-name {:optional true} string?]
   [:compact-views {:optional true} set?]
   [:release-tag-pattern {:optional true} string?]
   [:stable-tag-pattern {:optional true} string?]
   [:tag-patterns {:optional true} map?]
   [:projects {:optional true}
    [:map-of :string project-settings-schema]]
   [:test {:optional true} test-runner-config-schema]])

(defn validate-workspace-config [config]
  (-> workspace-schema
      (m/explain config)
      (me/humanize)))
