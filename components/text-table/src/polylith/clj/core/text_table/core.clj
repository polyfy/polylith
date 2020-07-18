(ns polylith.clj.core.text-table.core
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc.str :as str-util]
            [polylith.clj.core.util.interfc.color :as c]))

(defn row-lengths [row]
  (mapv #(-> % str c/clean-colors count) row))

(defn max-length [index lengths]
  (apply max (map #(nth % index) lengths)))

(defn none [_ & strings]
  (str/join strings))

(def color->function
  {:none none
   :yellow c/yellow
   :green c/green
   :blue c/blue
   :purple c/purple})

(defn align-str [[align max color value] color-mode]
  (let [cnt (- max (-> value str c/clean-colors count))
        cnt-left (quot cnt 2)
        cnt-right (- cnt cnt-left)
        color-fn (color->function color)
        spc (str-util/spaces cnt)
        spc-left (str-util/spaces cnt-left)
        spc-right (str-util/spaces cnt-right)]
    (condp = align
      :left (color-fn color-mode value spc)
      :right (color-fn color-mode spc value)
      :center (color-fn color-mode spc-left value spc-right)
      (str "error-align=" align))))

(defn align-row [data-row spc color-mode]
  (str spc (str/join (mapv #(align-str % color-mode) data-row))))

(defn ->lengths [rows]
  (let [all-row-lengths (mapv row-lengths rows)]
    (map #(max-length % all-row-lengths)
         (range 0 (-> rows first count)))))

(defn table-rows [spc alignments colors rows color-mode]
  (let [max-counts (repeat (->lengths rows))
        data-rows (mapv #(mapv vector %1 %2 %3 %4)
                        alignments max-counts colors rows)]
    (map #(align-row % spc color-mode) data-rows)))

(defn table [initial-spaces alignments colors rows color-mode]
  (str/join "\n" (table-rows initial-spaces alignments colors rows color-mode)))
