(ns ^:no-doc polylith.clj.core.workspace.fromdisk.bases-from-disk
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.common.interface.config :as config]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace.fromdisk.brick-dirs :as brick-dirs]
            [polylith.clj.core.workspace.fromdisk.brick-paths :as brick-paths]
            [polylith.clj.core.workspace.fromdisk.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace.fromdisk.non-top-namespace :as non-top-ns]))

(defn read-base [ws-dir ws-type ws-dialects user-home top-namespace ns-to-lib top-src-dir interface-ns brick->settings config]
  (let [deps-config (:deps config)
        base-name (:name config)
        base-dir (str ws-dir "/bases/" base-name)
        base-src-dirs (brick-dirs/top-src-dirs base-dir top-src-dir deps-config)
        base-test-dirs (brick-dirs/top-test-dirs base-dir top-src-dir deps-config)
        suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
        namespaces (ns-from-disk/namespaces-from-disk ws-dir ws-dialects base-src-dirs base-test-dirs suffixed-top-ns interface-ns)
        entity-root-path (str "bases/" base-name)
        lib-deps (lib/brick-lib-deps ws-dir ws-type deps-config top-namespace ns-to-lib namespaces entity-root-path user-home)
        source-paths (config/source-paths deps-config)
        non-top-namespaces (non-top-ns/non-top-namespaces "base" base-name base-dir top-src-dir source-paths)]
    (util/ordered-map :name base-name
                      :type "base"
                      :maven-repos (:mvn/repos deps-config)
                      :paths (brick-paths/source-paths base-dir deps-config)
                      :namespaces namespaces
                      :non-top-namespaces non-top-namespaces
                      :test (get-in brick->settings [base-name :test])
                      :necessary (get-in brick->settings [base-name :necessary])
                      :keep-lib-versions (get-in brick->settings [base-name :keep-lib-versions])
                      :lib-deps lib-deps)))

(defn read-bases
  [ws-dir ws-type ws-dialects user-home top-namespace ns-to-lib top-src-dir interface-ns base-dep-configs brick->settings]
  (vec (sort-by :name (map #(read-base ws-dir ws-type ws-dialects user-home top-namespace ns-to-lib top-src-dir interface-ns brick->settings %)
                           base-dep-configs))))
