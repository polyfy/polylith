(ns polylith.text-table.core
  (:require [clojure.string :as str]
            [polylith.util.interface.str :as str-util]))

(defn row-lengths [row]
  (mapv count row))

(defn max-length [index lengths]
  (apply max (map #(nth % index) lengths)))

(defn data-row [row-def row]
  (mapv conj row-def row))

(defn align-str [[align max string]]
  (let [cnt (- max (count string))
        cnt-left (quot cnt 2)
        cnt-right (- cnt cnt-left)
        spc (str-util/spaces cnt)
        spc-left (str-util/spaces cnt-left)
        spc-right (str-util/spaces cnt-right)]
    (condp = align
      :left (str string spc)
      :right (str spc string)
      :center (str spc-left string spc-right)
      "error")))

(defn alight-row [data-row]
  (str/join (mapv align-str data-row)))

(defn table-rows [headers alignments rows]
  (let [all-rows (conj rows headers)
        all-row-lengths (mapv row-lengths all-rows)
        lengths (map #(max-length % all-row-lengths)
                     (range 0 (count headers)))
        row-def (map vector alignments lengths)
        header-row (data-row row-def headers)
        data-rows (mapv #(data-row row-def %) rows)
        header (alight-row header-row)]
    (vec (concat [header]
                 [(str-util/line (count header))]
                 (map alight-row data-rows)))))

(defn table [headers alignments rows]
  (str/join "\n" (table-rows headers alignments rows)))
