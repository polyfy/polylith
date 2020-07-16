(ns polylith.clj.core.text-table.core
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc.str :as str-util]
            [polylith.clj.core.util.interfc.color :as c]))

(defn row-lengths [row]
  (mapv #(-> % str c/clean-colors count) row))

(defn max-length [index lengths]
  (apply max (map #(nth % index) lengths)))

(defn data-row [row-def row colors]
  (mapv conj row-def row colors))

(defn none [_ & strings]
  (str/join strings))

(def color->function
  {:none none
   :yellow c/yellow
   :green c/green
   :blue c/blue
   :purple c/purple})

(defn align-str [[align max value color] color-mode]
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

(defn lengths [headers rows]
  (let [all-rows (conj rows headers)
        all-row-lengths (mapv row-lengths all-rows)]
    (map #(max-length % all-row-lengths)
         (range 0 (count headers)))))

(defn table-rows [spc headers alignments rows header-colors row-colors color-mode]
  (let [lengths (lengths headers rows)
        row-def (map vector alignments lengths)
        header-row (data-row row-def headers header-colors)
        header (align-row header-row spc color-mode)
        line-cnt (- (count (align-row header-row spc c/none)) (count spc))
        data-rows (mapv #(data-row row-def %1 %2) rows row-colors)]
    (vec (concat [header]
                 [(str spc (str-util/line line-cnt))]
                 (map #(align-row % spc color-mode) data-rows)))))

(defn table [initial-spaces headers alignments rows header-colors row-colors color-mode]
  (str/join "\n" (table-rows initial-spaces headers alignments rows header-colors row-colors color-mode)))
