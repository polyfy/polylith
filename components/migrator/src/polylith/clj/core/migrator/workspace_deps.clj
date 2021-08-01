(ns polylith.clj.core.migrator.workspace-deps
  (:require [polylith.clj.core.migrator.shared :as shared]))

(defn create-config-file
  [ws-dir
   {:keys []
    {:keys [top-namespace
            interface-ns
            default-profile-name
            compact-views
            tag-patterns
            projects]} :settings}]
  (spit (str ws-dir "/workspace.edn")
    (shared/format-content "workspace"
                           [:top-namespace
                            :interface-ns
                            :default-profile-name
                            :compact-views
                            :vcs
                            :tag-patterns
                            :projects]
                           {:top-namespace top-namespace
                            :interface-ns (:name interface-ns)
                            :default-profile-name default-profile-name
                            :compact-views compact-views
                            :vcs {:name "git"
                                  :auto-add false}
                            :tag-patterns tag-patterns
                            :projects projects}))
  (println " - workspace.edn created"))
