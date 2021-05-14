(ns polylith.clj.core.lib.interface
  (:require [polylith.clj.core.lib.core :as core])
  (:require [polylith.clj.core.lib.text-table.lib-table :as lib-table]))

(defn with-sizes [library-map user-home]
  (core/with-sizes library-map user-home))

(defn brick-lib-deps [ws-type config top-namespace ns-to-lib namespaces user-home]
  (core/brick-lib-deps ws-type config top-namespace ns-to-lib namespaces user-home))

(defn print-lib-table [workspace is-all]
  (lib-table/print-table workspace is-all))
