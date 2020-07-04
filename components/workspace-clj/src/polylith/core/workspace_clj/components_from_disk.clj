(ns polylith.core.workspace-clj.components-from-disk
  (:require [clojure.string :as str]
            [polylith.core.file.interfc :as file]
            [polylith.core.workspace-clj.namespace :as namespace]
            [polylith.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.core.workspace-clj.interface-defs-from-disk :as defs-from-disk]))

(defn replace-underscore [string]
  (when string
    (str/replace string "_" "-")))

(defn read-the-component [ws-path component-name top-src-dir]
  (let [component-src-dir (str ws-path "/components/" component-name "/src/" top-src-dir)
        component-test-dir (str ws-path "/components/" component-name "/test/" top-src-dir)
        ; Only one folder should be in each components base src folder.
        ; The name of the folder will be the name of the interface,
        ; in case the component's name is not same as it's interface.
        interface-name (-> component-src-dir file/directory-paths first replace-underscore)
        src-dir (str component-src-dir interface-name)
        namespaces-src (ns-from-disk/namespaces-from-disk component-src-dir)
        namespaces-test (ns-from-disk/namespaces-from-disk component-test-dir)
        definitions (defs-from-disk/defs-from-disk src-dir)]
    {:name component-name
     :type "component"
     :top-namespace (namespace/->ns top-src-dir)
     :namespaces-src namespaces-src
     :namespaces-test namespaces-test
     :interface {:name interface-name
                 :definitions definitions}}))

(defn read-component [ws-path top-src-dirs component-name]
  "Reads a component from disk."
  (let [top-brick-src-dirs (namespace/top-brick-src-dirs ws-path "component" component-name top-src-dirs)]
    (if (= 1 (count top-brick-src-dirs))
      (read-the-component ws-path component-name (first top-brick-src-dirs))
      {:name component-name
       :type "component"
       :top-namespaces (mapv namespace/->ns top-brick-src-dirs)})))

(defn read-components [ws-path top-src-dirs]
  "Reads components from disk."
  (let [component-names (file/directory-paths (str ws-path "/components"))]
    (vec (sort-by :name (map #(read-component ws-path top-src-dirs %) component-names)))))
