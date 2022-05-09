(ns polylith.clj.core.workspace-clj.bases-from-disk
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace-clj.brick-paths :as brick-paths]
            [polylith.clj.core.workspace-clj.config-from-disk :as config-from-disk]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]))

(defn read-base [ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir brick->non-top-namespaces base-name]
  (let [base-dir (str ws-dir "/bases/" base-name)
        config (config-from-disk/read-config-file ws-type base-dir)
        brick-dirs (brick-paths/source-paths base-dir config)

        src-dir (str base-dir "/src/" top-src-dir)
        src-dirs (:src brick-dirs)

        test-dir (str base-dir "/test/" top-src-dir)
        test-dirs (:test brick-dirs)

        namespaces (ns-from-disk/namespaces-from-disk src-dir test-dir)
        entity-root-path (str "bases/" base-name)
        lib-deps (lib/brick-lib-deps ws-dir ws-type config top-namespace ns-to-lib namespaces entity-root-path user-home)]
    (println ":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::")
    (println (str src-dir))
    (println (str src-dirs))
    (println)
    (println (str test-dir))
    (println (str test-dirs))
    (println ":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::")
    (util/ordered-map :name base-name
                      :type "base"
                      :maven-repos (:mvn/repos config)
                      :paths (brick-paths/source-paths base-dir config)
                      :namespaces namespaces
                      :non-top-namespaces (brick->non-top-namespaces base-name)
                      :lib-deps lib-deps)))

(defn read-bases
  [ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir brick->non-top-namespaces]
  (vec (sort-by :name (map #(read-base ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir brick->non-top-namespaces %)
                           (file/directories (str ws-dir "/bases"))))))
