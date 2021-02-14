(ns polylith.clj.core.lib.interface
  (:require [polylith.clj.core.lib.core :as core])
  (:require [polylith.clj.core.lib.deps :as deps]
            [polylith.clj.core.lib.text-table.lib-table :as lib-table]))

(defn included-namespaces [top-namespace ns-to-lib namespaces-src]
  (deps/included-namespaces top-namespace ns-to-lib namespaces-src))

(defn with-sizes [library-map user-home]
  (core/with-sizes library-map user-home))

(defn brick-lib-deps-src [input-type config top-namespace ns-to-lib namespaces-src user-home dev-lib-deps]
  (core/lib-deps-src input-type config top-namespace ns-to-lib namespaces-src user-home dev-lib-deps))

(defn brick-lib-deps-test [input-type config top-namespace ns-to-lib namespaces-test user-home dev-lib-deps]
  (core/lib-deps-test input-type config top-namespace ns-to-lib namespaces-test user-home dev-lib-deps))

(defn print-lib-table [workspace is-all]
  (lib-table/print-table workspace is-all))
