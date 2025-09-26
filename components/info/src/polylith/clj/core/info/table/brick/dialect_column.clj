(ns polylith.clj.core.info.table.brick.dialect-column
  (:require [polylith.clj.core.text-table.interface :as text-table]))

(defn source-flags [source-types]
  (str (if (contains? source-types "clj") "j" "-")
       (if (contains? source-types "cljc") "c" "-")
       (if (contains? source-types "cljs") "s" "-")))

(defn dialect-cell [start-column index {:keys [source-types]}]
  (let [flags (source-flags source-types)]
    (text-table/cell start-column (+ index 3) flags :none :center)))

(defn column [is-show-dialect bricks]
  (when is-show-dialect
    (concat [(text-table/cell 5 "dialect")]
            (map-indexed #(dialect-cell 5 %1 %2)
                         bricks))))
