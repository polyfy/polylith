(ns polylith.clj.workspace-clj.bases-from-disk
  (:require [polylith.core.file.interfc :as file]
            [polylith.clj.workspace-clj.namespace :as namespace]
            [polylith.clj.workspace-clj.namespaces-from-disk :as ns-from-disk]))

(defn read-the-base [ws-path top-src-dir base-name]
  (let [src-dir (str ws-path "/bases/" base-name "/src/" top-src-dir)
        test-dir (str ws-path "/bases/" base-name "/test/" top-src-dir)
        namespaces-src (ns-from-disk/namespaces-from-disk src-dir)
        namespaces-test (ns-from-disk/namespaces-from-disk test-dir)]
    {:name base-name
     :type "base"
     :top-namespace (namespace/->ns top-src-dir)
     :namespaces-src namespaces-src
     :namespaces-test namespaces-test}))

(defn read-base [ws-path top-src-dirs base-name]
  (let [top-brick-src-dirs (namespace/top-brick-src-dirs ws-path "base" base-name top-src-dirs)]
    (if (= 1 (count top-brick-src-dirs))
      (read-the-base ws-path (first top-brick-src-dirs) base-name)
      {:name base-name
       :type "base"
       :top-namespaces (mapv namespace/->ns top-brick-src-dirs)})))

(defn read-bases [ws-path top-src-dirs]
  "Reads bases from disk"
  (let [base-names (file/directory-paths (str ws-path "/bases"))]
    (vec (sort-by :name (map #(read-base ws-path top-src-dirs %) base-names)))))
