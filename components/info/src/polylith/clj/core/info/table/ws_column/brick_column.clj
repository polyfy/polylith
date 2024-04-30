(ns ^:no-doc polylith.clj.core.info.table.ws-column.brick-column
  (:require [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.info.table.ws-column.shared :as shared]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn brick-name [alias name type changed? color-mode]
  (str (color/brick type (shared/full-name alias name) color-mode)
       (if changed? " *" "")))

(defn cell [index {:keys [alias name type changed?]} color-mode]
  (text-table/cell 3 (+ index 3)
                   (brick-name alias name type changed? color-mode)))

(defn column [bricks color-mode]
  (concat
    [(text-table/cell 3 "brick")]
    (map-indexed #(cell %1 %2 color-mode) bricks)))
