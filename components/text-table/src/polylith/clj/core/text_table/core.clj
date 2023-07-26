(ns ^:no-doc polylith.clj.core.text-table.core
  (:require [clojure.string :as str]
            [polylith.clj.core.text-table.flipper :as flipper]
            [polylith.clj.core.text-table.merger :as merger]
            [polylith.clj.core.text-table.table :as table]))

(defn table [initial-spaces color-mode cells-list]
  (let [cells (flipper/flip-to-map (merger/merge-list-of-cells cells-list))]
    (table/table initial-spaces cells color-mode)))

(defn print-table [table]
  (println (str/join "\n" table)))
