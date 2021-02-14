(ns polylith.clj.core.workspace-clj.bases-from-disk
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace-clj.config-from-disk :as config-from-disk]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]))

(defn read-base [ws-dir input-type user-home top-namespace ns-to-lib top-src-dir brick->non-top-namespaces dev-lib-deps base-name]
  (let [base-dir (str ws-dir "/bases/" base-name)
        src-dir (str base-dir "/src/" top-src-dir)
        test-dir (str base-dir "/test/" top-src-dir)
        namespaces-src (ns-from-disk/namespaces-from-disk src-dir)
        namespaces-test (ns-from-disk/namespaces-from-disk test-dir)
        config (config-from-disk/read-config-file input-type base-dir)
        lib-deps (lib/brick-lib-deps-src input-type config top-namespace ns-to-lib namespaces-src user-home dev-lib-deps)
        lib-deps-test (lib/brick-lib-deps-test input-type config top-namespace ns-to-lib namespaces-test user-home dev-lib-deps)]
    (util/ordered-map :name base-name
                      :type "base"
                      :namespaces-src namespaces-src
                      :namespaces-test namespaces-test
                      :non-top-namespaces (brick->non-top-namespaces base-name)
                      :lib-deps lib-deps
                      :lib-deps-test lib-deps-test)))

(defn read-bases [ws-dir input-type user-home top-namespace ns-to-lib top-src-dir brick->non-top-namespaces dev-lib-deps]
  "Reads bases from disk"
  (let [base-names (file/directories (str ws-dir "/bases"))]
    (vec (sort-by :name (map #(read-base ws-dir input-type user-home top-namespace ns-to-lib top-src-dir brick->non-top-namespaces dev-lib-deps %) base-names)))))
