(ns polylith.clj.core.workspace-clj.components-from-disk
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace-clj.interface-defs-from-disk :as defs-from-disk]))

(defn read-component [ws-dir top-src-dir component-name interface-ns brick->non-top-namespaces]
  (let [component-src-dir (str ws-dir "/components/" component-name "/src/" top-src-dir)
        component-test-dir (str ws-dir "/components/" component-name "/test/" top-src-dir)
        interface-path-name (-> component-src-dir file/directories first)
        interface-name (common/path-to-ns interface-path-name)
        src-dir (str component-src-dir interface-path-name)
        namespaces-src (ns-from-disk/namespaces-from-disk component-src-dir)
        namespaces-test (ns-from-disk/namespaces-from-disk component-test-dir)
        definitions (defs-from-disk/defs-from-disk src-dir interface-ns)
        interface-paths (defs-from-disk/interface-paths src-dir interface-ns :include-root? true)]
    (util/ordered-map :name component-name
                      :type "component"
                      :namespaces-src namespaces-src
                      :namespaces-test namespaces-test
                      :non-top-namespaces (brick->non-top-namespaces component-name)
                      :interface {:name interface-name
                                  :definitions definitions
                                  :paths (vec interface-paths)})))

(defn read-components [ws-dir top-src-dir component-names interface-ns brick->non-top-namespaces]
  (vec (sort-by :name (map #(read-component ws-dir top-src-dir % interface-ns brick->non-top-namespaces) component-names))))
