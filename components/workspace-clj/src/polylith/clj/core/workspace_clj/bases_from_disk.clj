(ns polylith.clj.core.workspace-clj.bases-from-disk
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.util.interface :as util]))

(defn read-base [ws-dir top-src-dir brick->non-top-namespaces base-name]
  (let [src-dir (str ws-dir "/bases/" base-name "/src/" top-src-dir)
        test-dir (str ws-dir "/bases/" base-name "/test/" top-src-dir)
        namespaces-src (ns-from-disk/namespaces-from-disk src-dir)
        namespaces-test (ns-from-disk/namespaces-from-disk test-dir)]
    (util/ordered-map :name base-name
                      :type "base"
                      :namespaces-src namespaces-src
                      :namespaces-test namespaces-test
                      :non-top-namespaces (brick->non-top-namespaces base-name))))

(defn read-bases
  "Reads bases from disk"
  [ws-dir top-src-dir brick->non-top-namespaces]
  (let [base-names (file/directories (str ws-dir "/bases"))]
    (vec (sort-by :name (map #(read-base ws-dir top-src-dir brick->non-top-namespaces %) base-names)))))
