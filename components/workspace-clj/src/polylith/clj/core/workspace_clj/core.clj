(ns polylith.clj.core.workspace-clj.core
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.user-config.interface :as user-config]
            [polylith.clj.core.version.interface :as version]
            [polylith.clj.core.path-finder.interface :as path-finder]
            [polylith.clj.core.workspace-clj.config :as config]
            [polylith.clj.core.workspace-clj.profile :as profile]
            [polylith.clj.core.workspace-clj.ws-reader :as ws-reader]
            [polylith.clj.core.workspace-clj.tag-pattern :as tag-pattern]
            [polylith.clj.core.workspace-clj.non-top-namespace :as non-top-ns]
            [polylith.clj.core.workspace-clj.bases-from-disk :as bases-from-disk]
            [polylith.clj.core.workspace-clj.project-settings :as project-settings]
            [polylith.clj.core.workspace-clj.projects-from-disk :as projects-from-disk]
            [polylith.clj.core.workspace-clj.components-from-disk :as components-from-disk]))

(defn stringify-key-value [[k v]]
  [(str k) (str v)])

(defn stringify [ws-type ns-to-lib]
  (when (not= ws-type :toolsdeps2)
    (into {} (mapv stringify-key-value ns-to-lib))))

(defn git-root []
  (let [[ok? root-path] (git/root-dir)]
    (if ok?
      root-path
      :no-git-root)))

(defn git-info [ws-dir {:keys [name auto-add]} tag-patterns {:keys [branch is-latest-sha]}]
  (let [from-branch (or branch git/branch)]
    {:name          name
     :branch        (git/current-branch)
     :git-root      (git-root)
     :auto-add      auto-add
     :stable-since  (git/sha ws-dir "stable" tag-patterns)
     :polylith      (cond-> {:repo git/repo
                             :branch from-branch}
                            is-latest-sha (assoc :latest-sha (or (git/latest-polylith-sha from-branch)
                                                                 "GIT-REPO-NOT-ACCESSIBLE")))}))

(defn ws-local-dir
  "Returns the directory/path to the workspace if it lives
   inside a git repository, or nil if the workspace and the
   git repository lives in the same directory."
  [ws-dir]
  (let [root-dir (git-root)]
    (when (and (not= ws-dir root-dir)
               (str/starts-with? ws-dir root-dir))
      (subs ws-dir (-> root-dir count inc)))))

(defn toolsdeps-ws-from-disk [ws-dir
                              ws-type
                              user-input
                              color-mode]
  (let [{:keys [aliases polylith]} (config/dev-config-from-disk ws-dir ws-type color-mode)
        ws-config (if (= :toolsdeps2 ws-type)
                    (config/ws-config-from-disk ws-dir color-mode)
                    (config/ws-config-from-dev polylith))
        {:keys [vcs top-namespace ws-type interface-ns default-profile-name tag-patterns release-tag-pattern stable-tag-pattern ns-to-lib compact-views]
         :or {vcs {:name "git", :auto-add false}
              compact-views {}}} ws-config
        patterns (tag-pattern/patterns tag-patterns stable-tag-pattern release-tag-pattern)
        interface-namespace (or interface-ns "interface")
        top-src-dir (-> top-namespace common/suffix-ns-with-dot common/ns-to-path)
        empty-character (user-config/empty-character)
        m2-dir (user-config/m2-dir)
        user-home (user-config/home-dir)
        thousand-separator (user-config/thousand-separator)
        user-config-filename (str (user-config/home-dir) "/.polylith/config.edn")
        brick->non-top-namespaces (non-top-ns/brick->non-top-namespaces ws-dir top-namespace)
        project->settings (project-settings/convert ws-config)
        projects (projects-from-disk/read-projects ws-dir ws-type project->settings user-input user-home color-mode)
        ns-to-lib-str (stringify ws-type (or ns-to-lib {}))
        components (components-from-disk/read-components ws-dir ws-type user-home top-namespace ns-to-lib-str top-src-dir interface-namespace brick->non-top-namespaces)
        bases (bases-from-disk/read-bases ws-dir ws-type user-home top-namespace ns-to-lib-str top-src-dir brick->non-top-namespaces)
        profile-to-settings (profile/profile-to-settings aliases user-home)
        paths (path-finder/paths ws-dir projects profile-to-settings)
        default-profile (or default-profile-name "default")
        active-profiles (profile/active-profiles user-input default-profile profile-to-settings)
        settings (util/ordered-map :vcs (git-info ws-dir vcs patterns user-input)
                                   :top-namespace top-namespace
                                   :interface-ns interface-namespace
                                   :default-profile-name default-profile
                                   :active-profiles active-profiles
                                   :tag-patterns patterns
                                   :color-mode color-mode
                                   :compact-views compact-views
                                   :user-config-filename user-config-filename
                                   :empty-character empty-character
                                   :thousand-separator thousand-separator
                                   :profile-to-settings profile-to-settings
                                   :projects project->settings
                                   :ns-to-lib ns-to-lib-str
                                   :user-home user-home
                                   :m2-dir m2-dir)]

    (util/ordered-map :ws-dir ws-dir
                      :ws-local-dir (ws-local-dir ws-dir)
                      :ws-reader ws-reader/reader
                      :user-input user-input
                      :settings settings
                      :components components
                      :bases bases
                      :projects projects
                      :paths paths
                      :version (version/version ws-type))))

(defn workspace-from-disk [user-input]
  (let [color-mode (or (:color-mode user-input) (user-config/color-mode) color/none)
        ws-dir (common/workspace-dir user-input color-mode)
        ws-type (cond
                  (file/exists (str ws-dir "/workspace.edn")) :toolsdeps2
                  (file/exists (str ws-dir "/deps.edn")) :toolsdeps1)]
    (case ws-type
      nil nil
      (toolsdeps-ws-from-disk ws-dir ws-type user-input color-mode))))
