(ns polylith.workspace-clj.bases-from-disk
  (:require [polylith.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.file.interface :as file]))

(defn read-base [ws-path top-src-dir base-name]
  (let [src-dir (str ws-path "/bases/" base-name "/src/" top-src-dir)
        test-dir (str ws-path "/bases/" base-name "/test/" top-src-dir)
        namespaces-src (ns-from-disk/namespaces-from-disk src-dir)
        test-namespaces (ns-from-disk/namespaces-from-disk test-dir)]
    {:name base-name
     :type "base"
     :namespaces-src namespaces-src
     :test-namespaces test-namespaces}))

(defn read-bases [ws-path top-src-dir]
  "Reads bases from disk"
  (let [base-names (file/directory-paths (str ws-path "/bases"))]
    (vec (sort-by :name (map #(read-base ws-path top-src-dir %) base-names)))))
