(ns ^:no-doc polylith.clj.core.workspace-clj.components-from-disk
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.common.interface.config :as config]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace-clj.brick-dirs :as brick-dirs]
            [polylith.clj.core.workspace-clj.brick-paths :as brick-paths]
            [polylith.clj.core.workspace-clj.non-top-namespace :as non-top-ns]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace-clj.interface-defs-from-disk :as defs-from-disk]))

(defn read-component [ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir interface-ns brick->settings deps-config]
  (let [config (:deps deps-config)
        component-name (:name deps-config)
        component-dir (str ws-dir "/components/" component-name)
        component-top-src-dirs (brick-dirs/top-src-dirs component-dir top-src-dir config)
        component-top-test-dirs (brick-dirs/top-test-dirs component-dir top-src-dir config)
        interface-path-name (first (mapcat file/directories component-top-src-dirs))
        interface-name (common/path-to-ns interface-path-name)
        src-dirs (mapv #(str % interface-path-name)
                       component-top-src-dirs)
        suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
        namespaces (ns-from-disk/namespaces-from-disk ws-dir component-top-src-dirs component-top-test-dirs suffixed-top-ns interface-ns)
        definitions (defs-from-disk/defs-from-disk src-dirs interface-ns)
        entity-root-path (str "components/" component-name)
        lib-deps (lib/brick-lib-deps ws-dir ws-type config top-namespace ns-to-lib namespaces entity-root-path user-home)
        paths (brick-paths/source-paths component-dir config)
        source-paths (config/source-paths config)
        component-settings (get brick->settings component-name)
        non-top-namespaces (non-top-ns/non-top-namespaces "component" component-name component-dir top-src-dir source-paths)]
    (util/ordered-map :name component-name
                      :type "component"
                      :maven-repos (:mvn/repos config)
                      :paths paths
                      :namespaces namespaces
                      :non-top-namespaces non-top-namespaces
                      :lib-deps lib-deps
                      :keep-lib-versions (:keep-lib-versions component-settings)
                      :interface (util/ordered-map :name interface-name
                                                   :definitions definitions))))

(defn read-components [ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir interface-ns component-dep-configs brick->settings]
  (vec (sort-by :name (map #(read-component ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir interface-ns brick->settings %)
                           component-dep-configs))))
