(ns polylith.clj.core.text-table.interfc
  (:require [polylith.clj.core.text-table.core :as core]
            [polylith.clj.core.text-table.line :as line]))

(defn line
  ([rows]
   (line/line rows))
  ([rows visables]
   (line/line rows visables)))

(defn table
  ([initial-spaces alignments colors rows color-mode]
   (core/table initial-spaces alignments colors rows color-mode))
  ([initial-spaces alignments header-colors header-orientations colors headers rows color-mode]
   (core/table initial-spaces alignments header-colors header-orientations colors headers (repeat true) rows color-mode))
  ([initial-spaces alignments header-colors header-orientations colors headers line-visables rows color-mode]
   (core/table initial-spaces alignments header-colors header-orientations colors headers line-visables rows color-mode)))
