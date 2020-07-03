(ns polylith.core.workspace-clj.bases-from-disk
  (:require [polylith.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.core.file.interfc :as file]))

(defn read-base [ws-path top-src-dir base-name]
  (let [src-dir (str ws-path "/bases/" base-name "/src/" top-src-dir)
        test-dir (str ws-path "/bases/" base-name "/test/" top-src-dir)
        namespaces-src (ns-from-disk/namespaces-from-disk src-dir)
        namespaces-test (ns-from-disk/namespaces-from-disk test-dir)]
    {:name base-name
     :type "base"
     :namespaces-src namespaces-src
     :namespaces-test namespaces-test}))

(defn read-bases [ws-path top-src-dir]
  "Reads bases from disk"
  (let [base-names (file/directory-paths (str ws-path "/bases"))]
    (vec (sort-by :name (map #(read-base ws-path top-src-dir %) base-names)))))
