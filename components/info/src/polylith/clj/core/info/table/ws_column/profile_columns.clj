(ns ^:no-doc polylith.clj.core.info.table.ws-column.profile-columns
  (:require [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.status :as status]))

(defn profile-cell [index brick-name column is-show-resources path-entries]
  (let [flags (status/brick-status-flags path-entries brick-name is-show-resources)]
    (text-table/cell column (+ index 3) flags :purple :center)))

(defn column [index profile start-column disk-paths bricks is-show-resources]
  (let [column (+ start-column (* 2 index))
        path-entries (extract/from-profiles-paths disk-paths profile)]
    (concat
      [(text-table/cell column 1 (:name profile) :purple :left)]
      (map-indexed #(profile-cell %1 %2 column is-show-resources path-entries)
                   (map :name bricks)))))

(defn columns [start-column bricks profiles disk-paths is-show-resources]
  (apply concat
         (map-indexed #(column %1 %2 start-column disk-paths bricks is-show-resources)
                      profiles)))
