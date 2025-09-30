(ns ^:no-doc polylith.clj.core.lib.interface
  (:require [polylith.clj.core.lib.core :as core]
            [polylith.clj.core.lib.size-deps :as size]
            [polylith.clj.core.lib.resolve-libs :as resolve-libs]
            [polylith.clj.core.lib.text-table.lib-table :as lib-table]
            [polylith.clj.core.lib.antq.outdated :as outdated]
            [polylith.clj.core.lib.antq.update :as update]))

(defn outdated-libs [library->latest-version]
  (outdated/outdated-libraries library->latest-version))

(defn lib-deps-with-latest-version [entity-name entity-type lib-deps outdated-libs lib->latest-version user-input name-type->keep-lib-versions]
  (outdated/lib-deps-with-latest-version lib-deps entity-name entity-type outdated-libs lib->latest-version user-input name-type->keep-lib-versions))

(defn latest-with-sizes [ws-dir entity-root-path libraries user-home]
  (core/latest-tools-deps-with-sizes ws-dir entity-root-path libraries user-home))

(defn with-sizes-vec [ws-dir entity-root-path libraries user-home]
  (size/with-sizes-vec ws-dir entity-root-path libraries user-home))

(defn brick-lib-deps [ws-dir ws-type deps-config package-config top-namespace ns-to-lib namespaces entity-root-path user-home]
  (core/brick-lib-deps ws-dir ws-type deps-config package-config  top-namespace ns-to-lib namespaces entity-root-path user-home))

(defn print-lib-table [workspace]
  (lib-table/print-table workspace))

(defn resolve-libs [src-deps override-deps]
  (resolve-libs/resolve-libs src-deps override-deps))

(defn update-libs! [workspace]
  (update/update-libs! workspace))

(defn table [workspace]
  (lib-table/table workspace))
