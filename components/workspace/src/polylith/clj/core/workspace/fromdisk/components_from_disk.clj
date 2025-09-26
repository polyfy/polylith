(ns ^:no-doc polylith.clj.core.workspace.fromdisk.components-from-disk
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.common.interface.config :as config]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace.fromdisk.brick-dirs :as brick-dirs]
            [polylith.clj.core.workspace.fromdisk.brick-paths :as brick-paths]
            [polylith.clj.core.workspace.fromdisk.non-top-namespace :as non-top-ns]
            [polylith.clj.core.workspace.fromdisk.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace.fromdisk.interface-defs-from-disk :as defs-from-disk]))

(defn read-component [ws-dir ws-type ws-dialects user-home top-namespace ns-to-lib top-src-dir interface-ns brick->settings config]
  (let [deps-config (:deps config)
        package-config (:package config)
        component-name (:name config)
        component-dir (str ws-dir "/components/" component-name)
        component-top-src-dirs (brick-dirs/top-src-dirs component-dir top-src-dir deps-config)
        component-top-test-dirs (brick-dirs/top-test-dirs component-dir top-src-dir deps-config)
        interface-path-name (first (mapcat file/directories component-top-src-dirs))
        interface-name (common/path-to-ns interface-path-name)
        suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
        namespaces (ns-from-disk/namespaces-from-disk ws-dir ws-dialects component-top-src-dirs component-top-test-dirs suffixed-top-ns interface-ns)
        source-types (common/source-types namespaces)
        definitions (defs-from-disk/defs-from-disk ws-dir ws-dialects top-namespace interface-name (:src namespaces) interface-ns)
        entity-root-path (str "components/" component-name)
        lib-deps (lib/brick-lib-deps ws-dir ws-type deps-config package-config top-namespace ns-to-lib namespaces entity-root-path user-home)
        paths (brick-paths/source-paths component-dir deps-config)
        source-paths (config/source-paths deps-config)
        non-top-namespaces (non-top-ns/non-top-namespaces "component" component-name component-dir top-src-dir source-paths)]
    (util/ordered-map :name component-name
                      :type "component"
                      :maven-repos (:mvn/repos deps-config)
                      :maven-local-repo (:mvn/local-repo deps-config)
                      :paths paths
                      :namespaces namespaces
                      :source-types source-types
                      :non-top-namespaces non-top-namespaces
                      :lib-deps lib-deps
                      :test (get-in brick->settings [component-name :test])
                      :necessary (get-in brick->settings [component-name :necessary])
                      :keep-lib-versions (get-in brick->settings [component-name :keep-lib-versions])
                      :interface (util/ordered-map :name interface-name
                                                   :definitions definitions))))

(defn read-components [ws-dir ws-type ws-dialects user-home top-namespace ns-to-lib top-src-dir interface-ns component-dep-configs brick->settings]
  (vec (sort-by :name (map #(read-component ws-dir ws-type ws-dialects user-home top-namespace ns-to-lib top-src-dir interface-ns brick->settings %)
                           component-dep-configs))))
