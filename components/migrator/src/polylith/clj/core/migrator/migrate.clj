(ns ^:no-doc polylith.clj.core.migrator.migrate
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.config-reader.interface :as config-reader]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.migrator.formatter :as formatter]))

(defn move-config [filepath dir content]
  (spit filepath content)
  (git/add dir "config.edn")
  (println (str "  Moved config to: " filepath)))

(defn create-brick-config [ws-dir {:keys [name type keep-lib-versions]}]
  (when keep-lib-versions
    (let [dir (str ws-dir "/" type "s/" name)
          filepath (str dir "/config.edn")]
      (move-config filepath dir {:keep-lib-versions keep-lib-versions}))))

(defn create-project-config [ws-dir {:keys [name alias is-dev necessary keep-lib-versions]} project->settings]
  (let [test (get-in project->settings [name :test])
        dir (if is-dev
               (str ws-dir "/development")
               (str ws-dir "/projects/" name))
        filepath (str dir "/config.edn")
        content (formatter/format-content name
                                          [:alias :necessary :keep-lib-versions :test]
                                          (cond-> {:alias alias}
                                               necessary (assoc :necessary necessary)
                                               keep-lib-versions (assoc :keep-lib-versions keep-lib-versions)
                                               test (assoc :test test)))]
    (move-config filepath dir content)))

(defn clean-workspace-config [ws-dir]
  (let [filepath (str ws-dir "/workspace.edn")
        {:keys [config error]} (config-reader/read-edn-file filepath "workspace.edn")]
    (if error
      (println error)
      (let [content (formatter/format-content "workspace"
                                              [:vcs
                                               :top-namespace
                                               :interface-ns
                                               :default-profile-name
                                               :compact-views
                                               :tag-patterns]
                                              (dissoc config :bricks :projects))]
        (spit filepath content)
        (println (str "  Removed keys from: " filepath))))))

(defn migrate-bricks [ws-dir bases components]
  (doseq [brick (concat bases components)]
    (create-brick-config ws-dir brick)))

(defn migrate-projects [ws-dir projects {:keys [workspaces]}]
  (let [project->settings (-> workspaces first :config :projects)]
    (doseq [project projects]
      (create-project-config ws-dir project project->settings))))

(defn migrate [ws-dir {:keys [bases components projects configs] :as workspace}]
  (if (common/need-migration? workspace)
    (do
      (migrate-bricks ws-dir bases components)
      (migrate-projects ws-dir projects configs)
      (clean-workspace-config ws-dir))
    (println "  The workspace is already migrated")))
