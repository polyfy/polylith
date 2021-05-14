(ns polylith.clj.core.workspace-clj.bases-from-disk
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace-clj.config-from-disk :as config-from-disk]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]))

(defn read-base [ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir brick->non-top-namespaces base-name]
  (let [base-dir (str ws-dir "/bases/" base-name)
        src-dir (str base-dir "/src/" top-src-dir)
        test-dir (str base-dir "/test/" top-src-dir)
        namespaces (ns-from-disk/namespaces-from-disk src-dir test-dir)
        config (config-from-disk/read-config-file ws-type base-dir)
        lib-deps (lib/brick-lib-deps ws-type config top-namespace ns-to-lib namespaces user-home)]
    (util/ordered-map :name base-name
                      :type "base"
                      :namespaces namespaces
                      :non-top-namespaces (brick->non-top-namespaces base-name)
                      :lib-deps lib-deps)))

(defn read-bases
  [ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir brick->non-top-namespaces]
  (vec (sort-by :name (map #(read-base ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir brick->non-top-namespaces %)
                           (file/directories (str ws-dir "/bases"))))))
