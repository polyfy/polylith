(ns ^:no-doc polylith.clj.core.image-creator.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure2d.core :as c2d]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.color :as color]))

(def font-size 20)
(def font-width 12) ; (c2d/char-width graphics \x)
(def font-height 24) ; (c2d/font-height graphics)

(defn set-text! [{:keys [color text x]} y color-mode graphics]
  (let [[r g b] (color/color->rgb color-mode color)]
    (c2d/set-color graphics r g b)
    (c2d/text graphics text x y)))

(defn set-text-row! [text x y font-width color-mode graphics]
  (doseq [text-info (color/split-colors text x font-width)]
    (set-text! text-info y color-mode graphics)))

(defn set-table! [table x y font-width font-height color-mode graphics]
  (doseq [[index text] (map-indexed vector table)]
    (when (-> text str/blank? not)
      (set-text-row! text x (+ y (* index font-height)) font-width color-mode graphics))))

(def font-resources-path "image-creator/JetBrainsMono-Medium.ttf")
(def font-path (str "components/image-creator-x/resources/" font-resources-path))

(defn load-font []
  (try
    (c2d/load-font (file/file font-path))
    (catch Exception _
      (c2d/load-font (io/resource font-resources-path)))))

(defn create-image [filename table canvas-areas transparent? color-mode]
  (let [width (+ 25 (* font-width (apply max (mapv #(-> % color/clean-colors count) table))))
        height (+ 45 (* font-height (count table)))
        font (load-font)
        canvas (c2d/canvas width height :high)
        graphics (c2d/make-graphics canvas)
        background (if (= "light" color-mode) [graphics 255 255 255] [graphics 36 39 43])]
    (doseq [{:keys [x y w h]} canvas-areas]
      (apply c2d/set-color background)
      (c2d/rect graphics x y w h))
    (when (nil? canvas-areas)
      (when (not transparent?)
        (apply c2d/set-background background)))
    (c2d/set-font graphics font)
    (c2d/set-font-attributes graphics font-size)
    (set-table! table 0 42 font-width font-height color-mode graphics)
    (c2d/save graphics filename)
    (c2d/flush-graphics graphics)))
