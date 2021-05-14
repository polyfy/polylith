(ns polylith.clj.core.workspace-clj.leiningen.core
  (:require [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.version.interface :as version]
            [polylith.clj.core.workspace-clj.ws-reader :as ws-reader]
            [polylith.clj.core.user-config.interface :as user-config]
            [polylith.clj.core.workspace-clj.non-top-namespace :as non-top-ns]
            [polylith.clj.core.workspace-clj.components-from-disk :as components-from-disk]
            [polylith.clj.core.workspace-clj.bases-from-disk :as bases-from-disk]))

(defn workspace-from-disk [ws-dir user-input]
  (let [top-namespace (:top-namespace (common/leiningen-config-key ws-dir :polylith))
        top-src-dir (-> top-namespace common/suffix-ns-with-dot common/ns-to-path)
        user-config-filename (str (user-config/home-dir) "/.polylith/config.edn")
        user-home (user-config/home-dir)
        m2-dir (user-config/m2-dir)
        interface-ns "interface"
        settings (util/ordered-map :version version/version
                                   :ws-type :leiningen1
                                   :ws-schema-version version/ws-schema-version
                                   :vcs "git"
                                   :top-namespace top-namespace
                                   :interface-ns interface-ns
                                   :default-profile-name "default"
                                   :active-profiles #{}
                                   :release-tag-pattern "v[0-9]*"
                                   :stable-tag-pattern "stable-*"
                                   :color-mode "dark"
                                   :compact-views #{}
                                   :user-config-filename user-config-filename
                                   :empty-character "."
                                   :thousand-separator ","
                                   :profile-to-settings {}
                                   :user-home user-home
                                   :m2-dir m2-dir)
        brick->non-top-namespaces (non-top-ns/brick->non-top-namespaces ws-dir top-namespace)
        components (components-from-disk/read-components ws-dir :leiningen1 user-home top-namespace {} top-src-dir interface-ns brick->non-top-namespaces)
        bases (bases-from-disk/read-bases ws-dir :leiningen1 user-home top-namespace {} top-src-dir brick->non-top-namespaces)]
    (util/ordered-map
                      :ws-dir ws-dir
                      :ws-reader ws-reader/reader
                      :user-input user-input
                      :settings settings
                      :components components
                      :bases bases)))
                     ;; todo: implement!
                     ;:projects projects
                     ;:paths paths)]))
