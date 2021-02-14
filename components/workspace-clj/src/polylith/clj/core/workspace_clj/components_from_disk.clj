(ns polylith.clj.core.workspace-clj.components-from-disk
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace-clj.config-from-disk :as config-from-disk]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace-clj.interface-defs-from-disk :as defs-from-disk]))

(defn read-component [ws-dir input-type user-home top-namespace ns-to-lib top-src-dir component-name interface-ns brick->non-top-namespaces dev-lib-deps]
  (let [component-dir (str ws-dir "/components/" component-name)
        component-src-dir (str component-dir "/src/" top-src-dir)
        component-test-dir (str component-dir "/test/" top-src-dir)
        interface-path-name (-> component-src-dir file/directories first)
        interface-name (common/path-to-ns interface-path-name)
        src-dir (str component-src-dir interface-path-name)
        namespaces-src (ns-from-disk/namespaces-from-disk component-src-dir)
        namespaces-test (ns-from-disk/namespaces-from-disk component-test-dir)
        definitions (defs-from-disk/defs-from-disk src-dir interface-ns)
        config (config-from-disk/read-config-file input-type component-dir)
        lib-deps (lib/brick-lib-deps-src input-type config top-namespace ns-to-lib namespaces-src user-home dev-lib-deps)
        lib-deps-test (lib/brick-lib-deps-test input-type config top-namespace ns-to-lib namespaces-test user-home dev-lib-deps)]
    (util/ordered-map :name component-name
                      :type "component"
                      :namespaces-src namespaces-src
                      :namespaces-test namespaces-test
                      :non-top-namespaces (brick->non-top-namespaces component-name)
                      :lib-deps lib-deps
                      :lib-deps-test lib-deps-test
                      :interface {:name interface-name
                                  :definitions definitions})))

(defn read-components [ws-dir input-type user-home top-namespace ns-to-lib top-src-dir component-names interface-ns brick->non-top-namespaces dev-lib-deps]
  (vec (sort-by :name (map #(read-component ws-dir input-type user-home top-namespace ns-to-lib top-src-dir % interface-ns brick->non-top-namespaces dev-lib-deps) component-names))))
