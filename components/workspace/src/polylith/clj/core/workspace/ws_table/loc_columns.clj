(ns polylith.clj.core.workspace.ws-table.loc-columns
  (:require [polylith.clj.core.workspace.ws-table.shared :as shared]))

(defn loc-column [header loc-key bricks column total-loc-bricks thousand-sep]
  (concat
    [(shared/header header column :none :right)]
    (map-indexed #(shared/number-cell %2 column (+ 3 %1) :right thousand-sep)
                 (map loc-key bricks))
    [(shared/number-cell total-loc-bricks column (+ (count bricks) 3) :right thousand-sep)]))

(defn columns [show-loc? bricks start-column total-loc-src-bricks total-loc-test-bricks thousand-sep]
  (when show-loc? (concat (loc-column "loc" :lines-of-code-src bricks start-column total-loc-src-bricks thousand-sep)
                          (loc-column "(t)" :lines-of-code-test bricks (+ 2 start-column) total-loc-test-bricks thousand-sep))))
