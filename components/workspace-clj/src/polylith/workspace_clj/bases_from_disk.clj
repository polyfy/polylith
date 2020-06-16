(ns polylith.workspace-clj.bases-from-disk
  (:require [polylith.workspace-clj.imports-from-disk :as imports-from-disk]
            [polylith.file.interface :as file]))

(defn read-base [ws-path top-src-dir base-name]
  (let [bases-src-dir (str ws-path "/bases/" base-name "/src/" top-src-dir)
        imports (imports-from-disk/all-imports bases-src-dir)]
    {:name base-name
     :type "base"
     :imports imports}))

(defn read-bases [ws-path top-src-dir]
  "Reads bases from disk"
  (let [base-names (file/directory-paths (str ws-path "/bases"))]
    (vec (sort-by :name (map #(read-base ws-path top-src-dir %) base-names)))))
