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
            [polylith.clj.core.workspace-clj.environment-from-disk :as envs-from-disk]
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

(defn workspace-from-disk
  ([user-input]
   (let [color-mode (or (:color-mode user-input) (user-config/color-mode) color/none)
         ws-dir (common/workspace-dir user-input color-mode)
         config (read-string (slurp (str ws-dir "/deps.edn")))
         message (validator/validate-dev-config config)]
     (if message
       (throw (ex-info (str "  " (color/error color-mode "Error in ./deps.edn: ") message) message))
       (workspace-from-disk ws-dir config user-input color-mode))))
  ([ws-dir {:keys [polylith aliases]} user-input color-mode]
   (let [{:keys [vcs top-namespace interface-ns default-profile-name release-tag-pattern stable-tag-pattern env-to-alias ns-to-lib compact-views]} polylith
         top-src-dir (-> top-namespace common/suffix-ns-with-dot common/ns-to-path)
         empty-char (user-config/empty-character)
         m2-dir (user-config/m2-dir)
         user-home (user-config/home-dir)
         thousand-sep (user-config/thousand-separator)
         user-config-file (str (user-config/home-dir) "/.polylith/config.edn")
         brick->non-top-namespaces (non-top-ns/brick->non-top-namespaces ws-dir top-namespace)
         component-names (file/directories (str ws-dir "/components"))
         components (components-from-disk/read-components ws-dir top-src-dir component-names interface-ns brick->non-top-namespaces)
         bases (bases-from-disk/read-bases ws-dir top-src-dir brick->non-top-namespaces)
         environments (envs-from-disk/read-environments ws-dir user-home color-mode)
         profile-to-settings (profile/profile-to-settings aliases user-home)
         paths (path-finder/paths ws-dir environments profile-to-settings)
         default-profile (or default-profile-name "default")
         active-profiles (profile/active-profiles user-input default-profile profile-to-settings)
         settings (util/ordered-map :version version/version
                                    :ws-schema-version version/ws-schema-version
                                    :vcs (or vcs "git")
                                    :top-namespace top-namespace
                                    :interface-ns (or interface-ns "interface")
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
                                    :env-to-alias env-to-alias
                                    :ns-to-lib (stringify ns-to-lib)
                                    :user-home user-home
                                    :m2-dir m2-dir)]
     (util/ordered-map :ws-dir ws-dir
                       :ws-reader ws-reader
                       :user-input user-input
                       :settings settings
                       :components components
                       :bases bases
                       :environments environments
                       :paths paths))))
