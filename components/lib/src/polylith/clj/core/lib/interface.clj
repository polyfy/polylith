(ns polylith.clj.core.lib.interface
  (:require [polylith.clj.core.lib.core :as core])
  (:require [polylith.clj.core.lib.deps :as deps]
            [polylith.clj.core.lib.text-table.lib-table :as lib-table]))

(defn dependencies [settings brick]
  (deps/dependencies settings brick))

(defn with-sizes [library-map user-home]
  (core/with-sizes library-map user-home))

(defn print-lib-table [workspace is-all]
  (lib-table/print-table workspace is-all))
