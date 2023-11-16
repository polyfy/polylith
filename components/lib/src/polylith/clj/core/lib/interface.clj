(ns ^:no-doc polylith.clj.core.lib.interface
  (:require [polylith.clj.core.antq.ifc :as antq]
            [polylith.clj.core.lib.core :as core]
            [polylith.clj.core.lib.size :as size]
            [polylith.clj.core.lib.outdated :as outdated]
            [polylith.clj.core.lib.used-libs :as used-libs]
            [polylith.clj.core.lib.resolve-libs :as resolve-libs]
            [polylith.clj.core.lib.text-table.lib-table :as lib-table]))

(defn outdated-libs [workspace]
  (outdated/outdated-libraries workspace))

(defn latest-with-sizes [ws-dir entity-root-path libraries user-home]
  (core/latest-with-sizes ws-dir entity-root-path libraries user-home))

(defn with-sizes-vec [ws-dir entity-root-path libraries user-home]
  (size/with-sizes-vec ws-dir entity-root-path libraries user-home))

(defn brick-lib-deps [ws-dir ws-type config top-namespace ns-to-lib namespaces entity-root-path user-home]
  (core/brick-lib-deps ws-dir ws-type config top-namespace ns-to-lib namespaces entity-root-path user-home))

(defn print-lib-table [workspace is-outdated]
  (lib-table/print-table workspace is-outdated))

(defn resolve-libs [src-deps override-deps]
  (resolve-libs/resolve-libs src-deps override-deps))

(defn type->name->lib->version [workspace]
  (used-libs/type->name->lib->version workspace))

(defn update-libs!
  "If libraries is empty, then update all libs, otherwise, only update selected libs."
  [workspace libraries-to-update color-mode]
  (let [type->name->lib->version (used-libs/type->name->lib->version workspace)]
    (antq/upgrade-libs! workspace libraries-to-update type->name->lib->version color-mode)))

(defn table [workspace]
  (let [outdated? (-> workspace :user-input :is-outdated)]
    (lib-table/table workspace outdated?)))
