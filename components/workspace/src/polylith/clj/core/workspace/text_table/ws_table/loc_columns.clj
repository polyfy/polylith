(ns polylith.clj.core.workspace.text-table.ws-table.loc-columns
  (:require [polylith.clj.core.workspace.text-table.shared :as shared]))

(defn loc-column [header loc-key bricks column thousand-sep]
  (let [total-loc (apply + (filter identity (map loc-key bricks)))]
    (concat
      [(shared/header header column :none :right)]
      (map-indexed #(shared/number-cell %2 column (+ 3 %1) :right thousand-sep)
                   (map loc-key bricks))
      [(shared/number-cell total-loc column (+ (count bricks) 3) :right thousand-sep)])))

(defn columns [show-loc? bricks start-column thousand-sep]
  (when show-loc? (concat (loc-column "loc" :lines-of-code-src bricks start-column thousand-sep)
                          (loc-column "(t)" :lines-of-code-test bricks (+ 2 start-column) thousand-sep))))
