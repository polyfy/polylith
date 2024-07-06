(ns ^:no-doc polylith.clj.core.image-creator.interface
  (:require [polylith.clj.core.image-creator.core :as core]))

(def font-width core/font-width) ;(c2d/char-width graphics \x)
(def font-height core/font-height) ; (c2d/font-height graphics))

(defn create-image [filename table canvas-areas color-mode]
  (core/create-image filename table canvas-areas color-mode))
