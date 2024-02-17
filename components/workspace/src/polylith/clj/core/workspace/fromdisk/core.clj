(ns ^:no-doc polylith.clj.core.workspace.fromdisk.core
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.config-reader.interface :as config-reader]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.user-config.interface :as user-config]
            [polylith.clj.core.version.interface :as version]
            [polylith.clj.core.path-finder.interface :as path-finder]
            [polylith.clj.core.workspace.fromdisk.profile :as profile]
            [polylith.clj.core.workspace.fromdisk.tag-pattern :as tag-pattern]
            [polylith.clj.core.workspace.fromdisk.ws-config :as ws-config]
            [polylith.clj.core.workspace.fromdisk.ws-reader :as ws-reader]
            [polylith.clj.core.workspace.fromdisk.bases-from-disk :as bases-from-disk]
            [polylith.clj.core.workspace.fromdisk.projects-from-disk :as projects-from-disk]
            [polylith.clj.core.workspace.fromdisk.components-from-disk :as components-from-disk]))

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

(defn git-info [ws-dir
                {:keys [name auto-add]
                 :or {name "git"
                      auto-add false}}
                tag-patterns
                {:keys [branch is-latest-sha]}]
  (let [git-repo? (git/is-git-repo? ws-dir)
        from-branch (or branch
                        ;; if we run this from the polylith repo, we want to use its branch.
                        (if (and is-latest-sha (git/is-polylith-repo? ws-dir))
                          (git/current-branch)
                          git/branch))]
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

(defn ->version [ws-type]
  (if (= :toolsdeps1 ws-type)
    (version/version {:ws {:breaking 0
                           :non-breaking 0}})
    (version/version)))

(defn toolsdeps-ws-from-disk [ws-name
                              ws-type
                              ws-dir
                              ws-config
                              aliases
                              user-input
                              color-mode]
  (let [{:keys [vcs top-namespace interface-ns default-profile-name tag-patterns release-tag-pattern stable-tag-pattern ns-to-lib compact-views test bricks workspaces]
         :or   {vcs {:name "git", :auto-add false}
                compact-views {}
                default-profile-name "default"
                interface-ns "interface"}} ws-config
        patterns (tag-pattern/patterns tag-patterns stable-tag-pattern release-tag-pattern)
        top-src-dir (-> top-namespace common/suffix-ns-with-dot common/ns-to-path)
        empty-character (user-config/empty-character)
        m2-dir (user-config/m2-dir)
        user-home (user-config/home-dir)
        thousand-separator (user-config/thousand-separator)
        user-config-filename (user-config/file-path)
        project->settings (:projects ws-config)
        ns-to-lib-str (stringify ws-type (or ns-to-lib {}))
        [component-configs component-errors] (config-reader/read-brick-config-file ws-dir ws-type "component")
        components (components-from-disk/read-components ws-dir ws-type user-home top-namespace ns-to-lib-str top-src-dir interface-ns component-configs bricks)
        [base-configs base-errors] (config-reader/read-brick-config-file ws-dir ws-type "base")
        bases (bases-from-disk/read-bases ws-dir ws-type user-home top-namespace ns-to-lib-str top-src-dir interface-ns base-configs bricks)
        name->brick (into {} (comp cat (map (juxt :name identity))) [components bases])
        suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
        [project-configs project-errors] (config-reader/read-project-config-file ws-dir ws-type)
        projects (projects-from-disk/read-projects ws-dir name->brick project->settings user-input user-home suffixed-top-ns interface-ns project-configs)
        profiles (profile/profiles ws-dir default-profile-name aliases name->brick user-home)
        ws-local-dir (->ws-local-dir ws-dir)
        paths (path-finder/paths ws-dir projects profiles)
        active-profiles (profile/active-profiles user-input default-profile-name profiles)
        config-errors (into [] cat [component-errors base-errors project-errors])
        version (->version ws-type)
        settings (util/ordered-map :vcs (git-info ws-dir vcs patterns user-input)
                                   :test test
                                   :top-namespace top-namespace
                                   :interface-ns interface-ns
                                   :default-profile-name default-profile-name
                                   :active-profiles active-profiles
                                   :tag-patterns patterns
                                   :color-mode color-mode
                                   :compact-views compact-views
                                   :user-config-filename user-config-filename
                                   :empty-character empty-character
                                   :thousand-separator thousand-separator
                                   :ns-to-lib ns-to-lib-str
                                   :user-home user-home
                                   :m2-dir m2-dir)]
    (util/ordered-map :name ws-name
                      :ws-type (name ws-type)
                      :ws-dir ws-dir
                      :ws-local-dir ws-local-dir
                      :ws-reader ws-reader/reader
                      :user-input user-input
                      :settings settings
                      :configs {:components component-configs
                                :bases      base-configs
                                :projects   (config-reader/clean-project-configs project-configs)
                                :user       (user-config/content)
                                :workspaces [{:name ws-name
                                              :type "workspace"
                                              :config ws-config}]}
                      :config-errors config-errors
                      :components components
                      :bases bases
                      :projects projects
;                      :workspaces workspaces
                      :paths paths
                      :profiles profiles
                      :version version)))

(defn workspace-name [ws-dir]
  (let [cleaned-ws-dir (if (= "." ws-dir) "" ws-dir)
        path (file/absolute-path cleaned-ws-dir)
        index (str/last-index-of path file/sep)]
    (cond-> path
            (some? index) (subs (inc index)))))

(defn create-workspace?
  "True if we try to create a workspace with a non-polylith deps.edn file.
   In that case, it should be possible to create a workspace."
  [{:keys [cmd args]}]
  (and (= "create" cmd)
       (= "workspace" (second args))))

(defn workspace-from-disk
  "Reads the workspace from disk, or from a file if 'ws-file'
   is given in the user-input, and stores it in a hash map."
  [user-input]
  (let [color-mode (or (:color-mode user-input) (user-config/color-mode) color/none)
        ws-dir (config-reader/workspace-dir user-input)
        ws-name (workspace-name ws-dir)
        ws-config-file (str ws-dir "/workspace.edn")
        deps-config-file (str ws-dir "/deps.edn")
        ws-type (cond
                  (config-reader/file-exists? ws-config-file :workspace) :toolsdeps2
                  (config-reader/file-exists? deps-config-file :development) :toolsdeps1)]
    (when ws-type
      (let [{:keys [deps error]} (config-reader/read-development-deps-config-file ws-dir ws-type)
            {:keys [aliases polylith]} deps
            [ws-config ws-error] (if (or error
                                         (= :toolsdeps2 ws-type))
                                   (ws-config/ws-config-from-disk ws-dir)
                                   (ws-config/ws-config-from-dev polylith))
            create-ws? (and ws-error
                            (create-workspace? user-input))]
        (cond
          create-ws? nil
          ws-error {:config-error ws-error}
          error {:config-error error}
          :else (toolsdeps-ws-from-disk ws-name ws-type ws-dir ws-config aliases user-input color-mode))))))



;
;(defn workspaces-from-disk [{:keys [workspaces] :as workspace}]
;  (cond-> workspace
;          workspaces (assoc)))