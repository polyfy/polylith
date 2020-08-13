(ns polylith.clj.core.text-table2.core
  (:require [polylith.clj.core.text-table2.table :as table]
            [polylith.clj.core.text-table2.merger :as merger]
            [polylith.clj.core.text-table2.flipper :as flipper]
            [clojure.string :as str]))

(defn table [initial-spaces color-mode cells-list]
  (let [cells (flipper/flip-to-map (merger/merge-list-of-cells cells-list))]
    (table/table initial-spaces cells color-mode)))

(defn print-table [table]
  (println (str/join "\n" table)))
