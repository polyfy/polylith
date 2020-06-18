(ns polylith.workspace-clj.bases-from-disk
  (:require [polylith.workspace-clj.imports-from-disk :as imports-from-disk]
            [polylith.file.interface :as file]))

(defn read-base [ws-path top-src-dir base-name]
  (let [bases-src-dir (str ws-path "/bases/" base-name "/src/" top-src-dir)
        namespaces (imports-from-disk/namespaces bases-src-dir)]
    {:name base-name
     :type "base"
     :namespaces namespaces}))

(defn read-bases [ws-path top-src-dir]
  "Reads bases from disk"
  (let [base-names (file/directory-paths (str ws-path "/bases"))]
    (vec (sort-by :name (map #(read-base ws-path top-src-dir %) base-names)))))
