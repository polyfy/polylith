(ns polylith.clj.core.workspace-clj.components-from-disk
  (:require [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace-clj.interface-defs-from-disk :as defs-from-disk]))

(defn read-component [ws-dir top-src-dir component-name interface-ns]
  (let [component-src-dir (str ws-dir "/components/" component-name "/src/" top-src-dir)
        component-test-dir (str ws-dir "/components/" component-name "/test/" top-src-dir)
        interface-path-name (-> component-src-dir file/directory-paths first)
        interface-name (common/path-to-ns interface-path-name)
        src-dir (str component-src-dir interface-path-name)
        namespaces-src (ns-from-disk/namespaces-from-disk component-src-dir)
        namespaces-test (ns-from-disk/namespaces-from-disk component-test-dir)
        definitions (defs-from-disk/defs-from-disk src-dir interface-ns)]
    {:name component-name
     :type "component"
     :namespaces-src namespaces-src
     :namespaces-test namespaces-test
     :interface {:name interface-name
                 :definitions definitions}}))

(defn read-components [ws-dir top-src-dir component-names interface-ns]
  (vec (sort-by :name (map #(read-component ws-dir top-src-dir % interface-ns) component-names))))
