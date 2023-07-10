(ns polylith.clj.core.image-creator.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure2d.core :as c2d]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.color :as color]))

(def font-size 20)
(def font-width 12) ;(c2d/char-width graphics \x)
(def font-height 24) ; (c2d/font-height graphics))

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

(def font-resources-path "image-creator/JetBrainsMono-Medium.ttf")
(def font-path (str "components/image-creator-x/resources/" font-resources-path))

(defn load-font []
  (try
    (c2d/load-font (file/file font-path))
    (catch Exception _
      (c2d/load-font (io/resource font-resources-path)))))

(defn create-image [filename table canvas-areas]
  (let [width (+ 25 (* font-width (apply max (mapv #(-> % color/clean-colors count) table))))
        height (+ 45 (* font-height (count table)))
        font (load-font)
        canvas (c2d/canvas width height :high)
        graphics (c2d/make-graphics canvas)]
    (doseq [{:keys [x y w h]} canvas-areas]
       (c2d/set-color graphics 36 39 43)
       (c2d/rect graphics x y w h))
    (if (nil? canvas-areas)
      (c2d/set-background graphics 36 39 43))
    (c2d/set-font graphics font)
    (c2d/set-font-attributes graphics font-size)
    (set-table! table 0 42 font-width font-height graphics)
    (c2d/save graphics filename)
    (c2d/flush-graphics graphics)))
