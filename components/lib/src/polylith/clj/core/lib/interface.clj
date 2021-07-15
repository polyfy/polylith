(ns polylith.clj.core.lib.interface
  (:require [polylith.clj.core.lib.core :as core]
            [polylith.clj.core.lib.size :as size]
            [polylith.clj.core.lib.resolve-libs :as resolve-libs]
            [polylith.clj.core.lib.text-table.lib-table :as lib-table]))

(defn latest-with-sizes [entity-root-path libraries user-home]
  (core/latest-with-sizes entity-root-path libraries user-home))

(defn with-sizes-vec [entity-root-path libraries user-home]
  (size/with-sizes-vec entity-root-path libraries user-home))

(defn brick-lib-deps [ws-type config top-namespace ns-to-lib namespaces entity-root-path user-home]
  (core/brick-lib-deps ws-type config top-namespace ns-to-lib namespaces entity-root-path user-home))

(defn print-lib-table [workspace is-all]
  (lib-table/print-table workspace is-all))

(defn resolve-libs [src-deps override-deps]
  (resolve-libs/resolve-libs src-deps override-deps))
