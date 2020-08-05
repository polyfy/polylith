(ns polylith.clj.core.workspace-clj.bases-from-disk
  (:require [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.file.interfc :as file]))

(defn read-base [ws-dir top-src-dir base-name]
  (let [src-dir (str ws-dir "/bases/" base-name "/src/" top-src-dir)
        test-dir (str ws-dir "/bases/" base-name "/test/" top-src-dir)
        namespaces-src (ns-from-disk/namespaces-from-disk src-dir)
        namespaces-test (ns-from-disk/namespaces-from-disk test-dir)]
    {:name base-name
     :type "base"
     :namespaces-src namespaces-src
     :namespaces-test namespaces-test}))

(defn read-bases [ws-dir top-src-dir]
  "Reads bases from disk"
  (let [base-names (file/directory-paths (str ws-dir "/bases"))]
    (vec (sort-by :name (map #(read-base ws-dir top-src-dir %) base-names)))))
