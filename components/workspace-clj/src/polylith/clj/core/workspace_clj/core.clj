(ns polylith.clj.core.workspace-clj.core
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.user-config.interface :as user-config]
            [polylith.clj.core.workspace-clj.profile :as profile]
            [polylith.clj.core.workspace-clj.bases-from-disk :as bases-from-disk]
            [polylith.clj.core.workspace-clj.environment-from-disk :as envs-from-disk]
            [polylith.clj.core.workspace-clj.components-from-disk :as components-from-disk]))

(def ws-reader
  {:name "polylith-clj"
   :project-url "https://github.com/tengstrand/polylith"
   :reader-version "1.0"
   :ws-contract-version 1
   :language "Clojure"
   :type-position "postfix"
   :slash "/"
   :file-extensions [".clj" "cljc"]})

(defn stringify-key-value [[k v]]
  [(str k) (str v)])

(defn stringify [ns->lib]
  (into {} (mapv stringify-key-value ns->lib)))

(defn workspace-from-disk
  ([user-input]
   (let [color-mode (or (:color-mode user-input) (user-config/color-mode) color/none)
         ws-dir (common/workspace-dir user-input color-mode)
         config (read-string (slurp (str ws-dir "/deps.edn")))]
     (workspace-from-disk ws-dir config user-input color-mode)))
  ([ws-dir {:keys [polylith aliases]} user-input color-mode]
   (let [{:keys [vcs top-namespace interface-ns default-profile-name stable-since-tag-pattern env->alias ns->lib]} polylith
         top-src-dir (-> top-namespace common/suffix-ns-with-dot common/ns-to-path)
         empty-char (user-config/empty-character)
         thousand-sep (user-config/thousand-separator)
         component-names (file/directories (str ws-dir "/components"))
         components (components-from-disk/read-components ws-dir top-src-dir component-names interface-ns)
         bases (bases-from-disk/read-bases ws-dir top-src-dir)
         environments (envs-from-disk/read-environments ws-dir)
         profile->settings (profile/profile->settings aliases)
         settings (util/ordered-map :vcs (or vcs "git")
                                    :top-namespace top-namespace
                                    :interface-ns (or interface-ns "interface")
                                    :default-profile-name (or default-profile-name "default")
                                    :stable-since-tag-pattern (or stable-since-tag-pattern "stable-*")
                                    :color-mode color-mode
                                    :empty-char (or empty-char ".")
                                    :thousand-sep (or thousand-sep ",")
                                    :profile->settings profile->settings
                                    :env->alias env->alias
                                    :ns->lib (stringify ns->lib)
                                    :user-input user-input)]
     (util/ordered-map :ws-dir ws-dir
                       :ws-reader ws-reader
                       :settings settings
                       :components components
                       :bases bases
                       :environments environments))))
