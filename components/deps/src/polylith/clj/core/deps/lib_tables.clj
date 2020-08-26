(ns polylith.clj.core.deps.lib-tables
  (:require [polylith.clj.core.deps.text-table.lib-table :as lib-table])
  (:require [polylith.clj.core.deps.text-table.lib-version-table :as lib-version-table]))

(defn print-lib-tables [workspace]
  (lib-table/print-table workspace)
  (println)
  (lib-version-table/print-table workspace))
