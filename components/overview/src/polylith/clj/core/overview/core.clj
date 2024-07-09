(ns ^:no-doc polylith.clj.core.overview.core
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.image-creator.interface :as ic]
            [polylith.clj.core.info.interface :as info]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn width [table]
  (-> table first color/clean-colors count))

(defn align-bottom [table max-height]
  (let [width (width table)
        height (count table)
        empty-row (str-util/spaces width)
        empty-rows (repeat (- max-height height) empty-row)]
    (concat empty-rows table)))

(defn join-row [& rows]
  (str/join rows))

(defn table [workspace]
  (let [info-table (info/workspace-table workspace)
        deps-table (deps/table workspace)
        lib-table (lib/table workspace)
        tables [info-table deps-table lib-table]
        table-width (apply + (map width tables))
        n#spaces (+ 4 (quot table-width 40))
        max-height (apply max (map count tables))
        empty-row (str-util/spaces n#spaces)
        empty-column (repeat max-height empty-row)]
    {:table (apply map join-row
                   (interpose empty-column (map #(align-bottom % max-height) tables)))
     :heights (mapv count tables)
     :widths (mapv width tables)
     :max-height max-height
     :n#spaces n#spaces}))

(defn canvas-area [index x1s heights max-height n#spaces]
  (let [spacing (* n#spaces index)
        x1 (x1s index)
        x2 (x1s (inc index))
        width (- x2 x1 -2)
        height (+ (heights index))]
    {:x (* (+ x1 spacing) ic/font-width)
     :y (* (- max-height height) ic/font-height)
     :w (* width ic/font-width)
     :h (* (+ height 2) ic/font-height)}))

(defn print-table [workspace]
  (let [{:keys [table heights widths max-height n#spaces]} (table workspace)
        x1s (mapv #(apply + (take % widths))
                  (range (inc (count widths))))
        canvas-areas (mapv #(canvas-area % x1s heights max-height n#spaces)
                           (range (count widths)))]
    (common/print-or-save-table workspace
                                #((constantly table) %)
                                canvas-areas nil)))

(comment
  (require '[polylith.clj.core.workspace.interface :as ws])
  (def input (assoc (user-input/extract-arguments ["overview" "ws-dir:examples/for-test"])
                    :is-transparent true
                    :out "overview.png"))
  (def workspace (ws/workspace input))

  (print-table workspace)
  #__)
