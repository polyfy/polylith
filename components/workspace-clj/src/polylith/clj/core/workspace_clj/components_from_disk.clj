(ns polylith.clj.core.workspace-clj.components-from-disk
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace-clj.brick-paths :as brick-paths]
            [polylith.clj.core.workspace-clj.config-from-disk :as config-from-disk]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace-clj.interface-defs-from-disk :as defs-from-disk]))


(defn read-component [ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir component-name interface-ns brick->non-top-namespaces]
  (let [component-dir (str ws-dir "/components/" component-name)
        config (config-from-disk/read-config-file ws-type component-dir)

        brick-dirs (brick-paths/source-paths component-dir config)

        component-src-dirs (brick-paths/make-brick-paths (:src brick-dirs) component-dir top-src-dir)
        component-test-dirs (brick-paths/make-brick-paths (:test brick-dirs) component-dir top-src-dir)

        interface-path-name (common/ns-to-path component-name)
        interface-name component-name

        src-dirs (brick-paths/make-src-dirs component-src-dirs interface-path-name)
        namespaces (brick-paths/make-namespaces component-src-dirs component-test-dirs)
        definitions (brick-paths/make-definitions src-dirs interface-ns)
        entity-root-path (str "components/" component-name)
        lib-deps (lib/brick-lib-deps ws-dir ws-type config top-namespace ns-to-lib namespaces entity-root-path user-home)]
    (util/ordered-map :name component-name
                      :type "component"
                      :maven-repos (:mvn/repos config)
                      :paths (brick-paths/source-paths component-dir config)
                      :namespaces namespaces
                      :non-top-namespaces (brick->non-top-namespaces component-name)
                      :lib-deps lib-deps
                      :interface (util/ordered-map :name interface-name :definitions definitions))))

(defn read-components [ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir interface-ns brick->non-top-namespaces]
  (vec (sort-by :name (map #(read-component ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir % interface-ns brick->non-top-namespaces)
                           (file/directories (str ws-dir "/components"))))))
