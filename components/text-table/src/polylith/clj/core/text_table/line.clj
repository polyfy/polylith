(ns polylith.clj.core.text-table.line
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc.color :as color]))

(defn max-cnt [rows index]
  (apply max (map #(-> (nth % index) color/clean-colors count) rows)))

(defn full-line [rows]
  (mapv #(str/join (repeat (max-cnt rows %) "-")) (range (-> rows first count))))
