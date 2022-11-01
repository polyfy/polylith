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
            [polylith.clj.core.workspace-clj.bases-from-disk :as bases-from-disk]
            [polylith.clj.core.workspace-clj.project-settings :as project-settings]
            [polylith.clj.core.workspace-clj.projects-from-disk :as projects-from-disk]
            [polylith.clj.core.workspace-clj.components-from-disk :as components-from-disk]))

(def no-git-repo "NO-GIT-REPO")

(defn stringify-key-value [[k v]]
  [(str k) (str v)])

(defn stringify [ws-type ns-to-lib]
  (when (not= ws-type :toolsdeps2)
    (into {} (map stringify-key-value) ns-to-lib)))

(defn git-root [git-repo?]
  (if git-repo?
    (let [[ok? root-path] (git/root-dir)]
      (if ok?
        root-path
        "GIT-ROOT-NOT-ACCESSIBLE"))
    no-git-repo))

(defn git-current-branch [git-repo?]
  (if git-repo?
    (git/current-branch)
    no-git-repo))

(defn git-sha [ws-dir since tag-patterns git-repo?]
  (if git-repo?
    (git/sha ws-dir since tag-patterns)
    {:sha no-git-repo}))

(defn git-latest-sha [from-branch git-repo?]
  (if git-repo?
    (or (git/latest-polylith-sha from-branch)
        "GIT-REPO-NOT-ACCESSIBLE")
    no-git-repo))

(defn git-info [ws-dir {:keys [name auto-add]
                        :or {name "git"
                             auto-add false}}
                tag-patterns {:keys [branch is-latest-sha]}]
  (let [git-repo? (git/is-git-repo? ws-dir)
        from-branch (or branch git/branch)]
    {:name          name
     :is-git-repo   git-repo?
     :branch        (git-current-branch git-repo?)
     :git-root      (git-root git-repo?)
     :auto-add      auto-add
     :stable-since  (git-sha ws-dir "stable" tag-patterns git-repo?)
     :polylith      (cond-> {:repo git/repo
                             :branch from-branch}
                            is-latest-sha (assoc :latest-sha (git-latest-sha from-branch git-repo?)))}))

(defn ->ws-local-dir
  "Returns the directory/path to the workspace if it lives
   inside a git repository, or nil if the workspace and the
   git repository lives in the same directory."
  [ws-dir]
  (let [git-repo? (git/is-git-repo? ws-dir)
        git-root-dir (git-root git-repo?)
        absolute-ws-dir (file/absolute-path ws-dir)]
    (when (and (not= absolute-ws-dir git-root-dir)
               (str/starts-with? absolute-ws-dir git-root-dir))
      (subs absolute-ws-dir (-> git-root-dir count inc)))))

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
              compact-views {}
              interface-ns "interface"}} ws-config
        patterns (tag-pattern/patterns tag-patterns stable-tag-pattern release-tag-pattern)
        top-src-dir (-> top-namespace common/suffix-ns-with-dot common/ns-to-path)
        empty-character (user-config/empty-character)
        m2-dir (user-config/m2-dir)
        user-home (user-config/home-dir)
        thousand-separator (user-config/thousand-separator)
        user-config-filename (user-config/config-file-path)
        project->settings (project-settings/convert ws-config)
        ns-to-lib-str (stringify ws-type (or ns-to-lib {}))
        components (components-from-disk/read-components ws-dir ws-type user-home top-namespace ns-to-lib-str top-src-dir interface-ns)
        bases (bases-from-disk/read-bases ws-dir ws-type user-home top-namespace ns-to-lib-str top-src-dir interface-ns)
        name->brick (into {} (comp cat (map (juxt :name identity))) [components bases])
        suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
        projects (projects-from-disk/read-projects ws-dir ws-type name->brick project->settings user-input user-home suffixed-top-ns interface-ns)
        profile-to-settings (profile/profile-to-settings ws-dir aliases name->brick user-home)
        ws-local-dir (->ws-local-dir ws-dir)
        paths (path-finder/paths ws-dir projects profile-to-settings)
        default-profile (or default-profile-name "default")
        active-profiles (profile/active-profiles user-input default-profile profile-to-settings)
        settings (util/ordered-map :vcs (git-info ws-dir vcs patterns user-input)
                                   :top-namespace top-namespace
                                   :interface-ns interface-ns
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
                      :ws-local-dir ws-local-dir
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
    (when ws-type
      (toolsdeps-ws-from-disk ws-dir ws-type user-input color-mode))))
