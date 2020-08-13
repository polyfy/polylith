(ns polylith.clj.core.workspace.ws-table.brick-column
  (:require [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.workspace.ws-table.shared :as shared]))

(defn brick-name [name type changed-bricks color-mode]
  (let [changed (if (contains? changed-bricks name) " *" "")]
    (str (color/brick type name color-mode) changed)))

(defn brick-cell [index {:keys [name type]} changed-bricks color-mode]
  (let [brick (brick-name name type changed-bricks color-mode)]
    (shared/standard-cell brick 3 (+ index 3))))

(defn column [bricks {:keys [changed-components changed-bases]} color-mode]
  (let [changed-bricks (set (concat changed-components changed-bases))]
    (concat
      (map-indexed #(brick-cell %1 %2 changed-bricks color-mode)
                   bricks))))
