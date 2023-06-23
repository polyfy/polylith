(ns polylith.clj.core.image-creator.core
  (:require [clojure.string :as str]
            [clojure2d.core :as c2d]
            [polylith.clj.core.util.interface.color :as color]))

(def font-name "Courier New")
(def font-size 20)
(def font-width 12) ;(c2d/char-width graphics \x)
(def font-height 23) ; (c2d/font-height graphics))

(defn set-text! [{:keys [color text x]} y graphics]
  (let [[r g b] (color/color->rgb color)]
    (c2d/set-color graphics r g b)
    (c2d/text graphics text x y)))

(defn set-text-row! [text x y font-width graphics]
  (doseq [text-info (color/split-colors text x font-width)]
    (set-text! text-info y graphics)))

(defn set-table! [table x y font-width font-height graphics]
  (doseq [[index text] (map-indexed vector table)]
    (when (-> text str/blank? not)
      (set-text-row! text x (+ y (* index font-height)) font-width graphics))))

(defn create-image [filename table]
  (let [width (+ 25 (* font-width (apply max (mapv #(-> % color/clean-colors count) table))))
        height (+ 40 (* font-height (count table)))
        canvas (c2d/canvas width height :high)
        graphics (c2d/make-graphics canvas)]
    (c2d/set-background graphics 36 39 43)
    (c2d/set-font graphics font-name)
    (c2d/set-font-attributes graphics font-size)
    (set-table! table 0 37 font-width font-height graphics)
    (c2d/save graphics filename)
    (c2d/flush-graphics graphics)))
