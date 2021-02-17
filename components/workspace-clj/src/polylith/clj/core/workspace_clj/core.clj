(ns polylith.clj.core.workspace-clj.core
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.user-config.interface :as user-config]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.version.interface :as version]
            [polylith.clj.core.path-finder.interface :as path-finder]
            [polylith.clj.core.workspace-clj.profile :as profile]
            [polylith.clj.core.workspace-clj.non-top-namespace :as non-top-ns]
            [polylith.clj.core.workspace-clj.bases-from-disk :as bases-from-disk]
            [polylith.clj.core.workspace-clj.projects-from-disk :as projects-from-disk]
            [polylith.clj.core.workspace-clj.components-from-disk :as components-from-disk]))

(def ws-reader
  {:name "polylith-clj"
   :project-url "https://github.com/polyfy/polylith"
   :language "Clojure"
   :type-position "postfix"
   :slash "/"
   :file-extensions [".clj" "cljc"]})

(defn stringify-key-value [[k v]]
  [(str k) (str v)])

(defn stringify [ns-to-lib]
  (into {} (mapv stringify-key-value ns-to-lib)))

(defn dev-config-from-disk [ws-dir input-type color-mode]
  (let [config (read-string (slurp (str ws-dir "/deps.edn")))
        message (validator/validate-dev-config input-type config)]
    (if message
      (throw (ex-info (str "  " (color/error color-mode "Error in ./deps.edn: ") message) message))
      config)))

(defn ws-config-from-disk [ws-path color-mode]
  (let [config (read-string (slurp ws-path))
        message (validator/validate-workspace-config config)]
    (if message
      (throw (ex-info (str "  " (color/error color-mode "Error in ./worspace.edn: ") message) message))
      config)))

(defn workspace-from-disk
  ([user-input]
   (let [color-mode (or (:color-mode user-input) (user-config/color-mode) color/none)
         ws-dir (common/workspace-dir user-input color-mode)
         ws-path (str ws-dir "/workspace.edn")
         input-type (if (file/exists ws-path) :toolsdeps2 :toolsdeps1)
         dev-config (dev-config-from-disk ws-dir input-type color-mode)
         ws-config (if (= :toolsdeps2 input-type)
                     (ws-config-from-disk ws-path color-mode)
                     (assoc (:polylith dev-config) :input-type :toolsdeps1))]
     (workspace-from-disk ws-dir input-type ws-config dev-config user-input color-mode)))
  ([ws-dir
    input-type
    {:keys [vcs top-namespace input-type interface-ns default-profile-name release-tag-pattern stable-tag-pattern project-to-alias ns-to-lib compact-views]}
    {:keys [aliases]}
    user-input
    color-mode]
   (let [interface-namespace (or interface-ns "interface")
         top-src-dir (-> top-namespace common/suffix-ns-with-dot common/ns-to-path)
         empty-char (user-config/empty-character)
         m2-dir (user-config/m2-dir)
         user-home (user-config/home-dir)
         thousand-sep (user-config/thousand-sep)
         user-config-file (str (user-config/home-dir) "/.polylith/config.edn")
         brick->non-top-namespaces (non-top-ns/brick->non-top-namespaces ws-dir top-namespace)
         component-names (file/directories (str ws-dir "/components"))
         projects (projects-from-disk/read-projects ws-dir user-home color-mode)
         ns-to-lib-str (stringify (or ns-to-lib {}))
         components (components-from-disk/read-components ws-dir input-type user-home top-namespace ns-to-lib-str top-src-dir component-names interface-namespace brick->non-top-namespaces)
         bases (bases-from-disk/read-bases ws-dir input-type user-home top-namespace ns-to-lib-str top-src-dir brick->non-top-namespaces)
         profile-to-settings (profile/profile-to-settings aliases user-home)
         paths (path-finder/paths ws-dir projects profile-to-settings)
         default-profile (or default-profile-name "default")
         active-profiles (profile/active-profiles user-input default-profile profile-to-settings)
         settings (util/ordered-map :version version/version
                                    :input-type input-type
                                    :ws-schema-version version/ws-schema-version
                                    :vcs (or vcs "git")
                                    :top-namespace top-namespace
                                    :interface-ns interface-namespace
                                    :default-profile-name default-profile
                                    :active-profiles active-profiles
                                    :release-tag-pattern (or release-tag-pattern "v[0-9]*")
                                    :stable-tag-pattern (or stable-tag-pattern "stable-*")
                                    :color-mode color-mode
                                    :compact-views (or compact-views #{})
                                    :user-config-file user-config-file
                                    :empty-char (or empty-char ".")
                                    :thousand-sep (or thousand-sep ",")
                                    :profile-to-settings profile-to-settings
                                    :project-to-alias (or project-to-alias {})
                                    :ns-to-lib ns-to-lib-str
                                    :user-home user-home
                                    :m2-dir m2-dir)]
     (util/ordered-map :ws-dir ws-dir
                       :ws-reader ws-reader
                       :user-input user-input
                       :settings settings
                       :components components
                       :bases bases
                       :projects projects
                       :paths paths))))
