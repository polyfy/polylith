(ns polylith.clj.core.overview.core
  (:require [clojure.string :as str]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.image-creator.interface :as image-creator]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.workspace.interface :as ws]))

(defn align-bottom [table max-height]
  (let [width (-> table first color/clean-colors count)
        height (count table)
        empty-row (str-util/spaces width)
        empty-rows (repeat (- max-height height) empty-row)]
    (concat empty-rows table)))

(defn join-row [& rows]
  (str/join rows))

(defn table [workspace]
  (let [info-table (ws/table workspace)
        deps-table (deps/table workspace)
        lib-table (lib/table workspace)
        tables [info-table deps-table lib-table]
        max-height (apply max (map count tables))
        empty-row (str-util/spaces 10)
        empty-column (repeat max-height empty-row)]
    (apply map join-row
           (interpose empty-column (map #(align-bottom % max-height) tables)))))

(defn create-image [{:keys [image] :as workspace}]
  (let [table (table workspace)
        filename (or image "overview.png")]
    (image-creator/create-image filename table)))

(comment
  (def input (user-input/extract-params (concat ["overview" "ws-dir:examples/for-test"])))
  (def workspace (-> input
                     ws-clj/workspace-from-disk
                     ws/enrich-workspace
                     change/with-changes))

  (text-table/print-table (table workspace))
  #__)
