(ns ^:no-doc polylith.clj.core.text-table.table
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface.color :as c]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn none [_ & strings]
  (str/join strings))

(def color->function
  {:none none
   :cyan c/cyan
   :grey c/grey
   :yellow c/yellow
   :green c/green
   :blue c/blue
   :purple c/purple})

(defn align [[[x y] {:keys [value align color]}] max-width color-mode]
  (let [cnt (- max-width (-> value str c/clean-colors count))
        cnt-left (quot cnt 2)
        cnt-right (- cnt cnt-left)
        color-fn (color->function color)
        spc (str-util/spaces cnt)
        spc-left (str-util/spaces cnt-left)
        spc-right (str-util/spaces cnt-right)
        new-value (condp = align
                    :left (color-fn color-mode value spc)
                    :right (color-fn color-mode spc value)
                    :center (color-fn color-mode spc-left value spc-right)
                    (str "Error. Can't find alignment: " align))]
    [[x y] {:value new-value}]))

(defn column [x cells]
  (filter #(= x (ffirst %)) cells))

(defn max-column-width [x cells]
  (apply max (mapv #(-> % second :value c/clean-colors count)
                   (column x cells))))

(defn align-column [x cells color-mode]
  (let [max-width (max-column-width x cells)]
    (map #(align % max-width color-mode)
         (column x cells))))

(defn align-table [cells color-mode]
  (into {} (mapcat #(align-column % cells color-mode) (set (map ffirst cells)))))

(defn value [x y x->spaces cells]
  (if-let [{:keys [value]} (cells [x y])]
    value
    (or (x->spaces x) "#ERROR#")))

(defn row [initial-spaces y xs x->spaces cells]
  (str initial-spaces (str/join (mapv #(value % y x->spaces cells) xs))))

(defn table [initial-spaces cells color-mode]
  (let [aligned-cells (align-table cells color-mode)
        xs (sort (set (map ffirst aligned-cells)))
        ys (sort (set (map #(-> % first second) aligned-cells)))
        x->spaces (into {} (map (juxt identity #(str-util/spaces (max-column-width % aligned-cells))) xs))]
    (mapv #(row initial-spaces % xs x->spaces aligned-cells) ys)))

