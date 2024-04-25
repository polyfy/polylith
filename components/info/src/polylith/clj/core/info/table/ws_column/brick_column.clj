(ns ^:no-doc polylith.clj.core.info.table.ws-column.brick-column
  (:require [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn brick-name [name type changed-bricks color-mode]
  (let [changed (if (contains? changed-bricks name) " *" "")]
    (str (color/brick type name color-mode) changed)))

(defn ws-brick-name [alias name type changed-bricks color-mode]
  (let [changed (if (contains? changed-bricks name) " *" "")]
    (str (color/brick type (str alias "/" name) color-mode) changed)))

(defn brick-cell [index start-row {:keys [name type]} changed-bricks color-mode]
  (let [brick (brick-name name type changed-bricks color-mode)]
    (text-table/cell 3 (+ index start-row 3) brick)))

(defn ws-component-cell [index start-row {:keys [alias name type]} alias->workspace color-mode]
  (let [changed-components (set (-> alias alias->workspace :changes :changed-components))
        component (ws-brick-name alias name type changed-components color-mode)]
    (text-table/cell 3 (+ index start-row 3) component)))

(defn ws-base-cell [index start-row {:keys [alias name type]} alias->workspace color-mode]
  (let [changed-bases (set (-> alias alias->workspace :changes :changed-bases))
        base (ws-brick-name alias name type changed-bases color-mode)]
    (text-table/cell 3 (+ index start-row 3) base)))

(defn column [components
              bases
              ws-components
              ws-bases
              alias->workspace
              {:keys [changed-components changed-bases]}
              color-mode]
  (let [changed-bricks (set (concat changed-components changed-bases))
        start-row1 (+ (count components))
        start-row2 (+ start-row1 (count ws-components))
        start-row3 (+ start-row2 (count bases))]
    (concat
      [(text-table/cell 3 "brick")]
      (map-indexed #(brick-cell %1 0 %2 changed-bricks color-mode) components)
      (map-indexed #(ws-component-cell %1 start-row1 %2 alias->workspace color-mode) ws-components)
      (map-indexed #(brick-cell %1 start-row2 %2 changed-bricks color-mode) bases)
      (map-indexed #(ws-base-cell %1 start-row3 %2 alias->workspace color-mode) ws-bases))))
