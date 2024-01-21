(ns ^:no-doc polylith.clj.core.info.table.ws-column.loc-columns
  (:require [polylith.clj.core.text-table.interface :as text-table]))

(defn loc-column [header loc-key bricks column thousand-separator]
  (let [total-loc (apply + (filter identity (map #(-> % :lines-of-code loc-key) bricks)))]
    (concat
      [(text-table/cell column 1 header :none :right)]
      (map-indexed #(text-table/number-cell column (+ 3 %1) %2 :right thousand-separator)
                   (map #(-> % :lines-of-code loc-key) bricks))
      [(text-table/number-cell column (+ (count bricks) 3) total-loc :right thousand-separator)])))

(defn columns [is-show-loc bricks start-column thousand-separator]
  (when is-show-loc (concat (loc-column "loc" :src bricks start-column thousand-separator)
                            (loc-column "(t)" :test bricks (+ 2 start-column) thousand-separator))))
