(ns polylith.clj.core.text-table.line
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc.color :as color]))

(defn max-cnt [rows index]
  (apply max (map #(-> (nth % index) color/clean-colors count) rows)))

(defn sub-line [rows index visible?]
  (str/join (repeat (max-cnt rows index) (if visible? "-" " "))))

(defn line
  ([rows]
   (line rows (repeat :true)))
  ([rows visables]
   (mapv #(sub-line rows %1 %2)
         (range (-> rows first count))
         visables)))
