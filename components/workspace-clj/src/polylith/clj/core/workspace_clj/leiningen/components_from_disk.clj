(ns polylith.clj.core.workspace-clj.leiningen.components-from-disk
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.workspace-clj.config-from-disk :as config-from-disk]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace-clj.interface-defs-from-disk :as defs-from-disk]))

(defn read-component [ws-dir ws-type top-src-dir interface-ns brick->non-top-namespaces component-name]
  (let [component-dir (str ws-dir "/components/" component-name)
        component-src-dir (str component-dir "/src/" top-src-dir)
        component-test-dir (str component-dir "/test/" top-src-dir)
        interface-path-name (-> component-src-dir file/directories first)
        interface-name (common/path-to-ns interface-path-name)
        src-dir (str component-src-dir interface-path-name)
        namespaces (ns-from-disk/namespaces-from-disk component-src-dir component-test-dir)
        definitions (defs-from-disk/defs-from-disk src-dir interface-ns)
        config (config-from-disk/read-config-file ws-type component-dir)]
    (util/ordered-map :name component-name
                      :type "component"
                      :namespaces namespaces
                      :non-top-namespaces (brick->non-top-namespaces component-name)
                      ;; todo: implement!
                      ;:lib-deps lib-deps
                      :interface {:name interface-name
                                  :definitions definitions})))

(defn read-components [ws-dir ws-type top-src-dir interface-ns brick->non-top-namespaces]
  (map #(read-component ws-dir ws-type top-src-dir interface-ns brick->non-top-namespaces %)
       (file/directories (str ws-dir "/components"))))
