(ns ^:no-doc polylith.clj.core.info.table.ws-column.brick-column
  (:require [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn brick-name [name type changed-bricks color-mode]
  (let [changed (if (contains? changed-bricks name) " *" "")]
    (str (color/brick type name color-mode) changed)))

(defn brick-cell [index {:keys [name type]} changed-bricks color-mode]
  (let [brick (brick-name name type changed-bricks color-mode)]
    (text-table/cell 3 (+ index 3) brick)))

(defn column [bricks {:keys [changed-components changed-bases]} color-mode]
  (let [changed-bricks (set (concat changed-components changed-bases))]
    (concat
      [(text-table/cell 3 "brick")]
      (map-indexed #(brick-cell %1 %2 changed-bricks color-mode)
                   bricks))))
