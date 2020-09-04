(ns polylith.clj.core.workspace.text-table.ws-table-column.loc-columns
  (:require [polylith.clj.core.text-table.interface :as text-table]))

(defn loc-column [header loc-key bricks column thousand-sep]
  (let [total-loc (apply + (filter identity (map loc-key bricks)))]
    (concat
      [(text-table/cell column 1 header :none :right)]
      (map-indexed #(text-table/number-cell column (+ 3 %1) %2 :right thousand-sep)
                   (map loc-key bricks))
      [(text-table/number-cell column (+ (count bricks) 3) total-loc :right thousand-sep)])))

(defn columns [show-loc? bricks start-column thousand-sep]
  (when show-loc? (concat (loc-column "loc" :lines-of-code-src bricks start-column thousand-sep)
                          (loc-column "(t)" :lines-of-code-test bricks (+ 2 start-column) thousand-sep))))
