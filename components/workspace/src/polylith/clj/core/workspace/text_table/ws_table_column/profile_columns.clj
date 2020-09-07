(ns polylith.clj.core.workspace.text-table.ws-table-column.profile-columns
  (:require [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.status :as status]))

(defn profile-cell [index brick-name column show-resources? path-entries]
  (let [flags (status/brick-status-flags path-entries brick-name show-resources?)]
    (text-table/cell column (+ index 3) flags :purple :center)))

(defn column [index profile start-column disk-paths settings bricks show-resources?]
  (let [column (+ start-column (* 2 index))
        path-entries (extract/from-profiles-paths disk-paths settings profile)]
    (concat
      [(text-table/cell column 1 (name profile) :purple :left)]
      (map-indexed #(profile-cell %1 %2 column show-resources? path-entries)
                   (map :name bricks)))))

(defn columns [start-column bricks profiles disk-paths settings show-resources?]
  (apply concat
    (map-indexed #(column %1 %2 start-column disk-paths settings bricks show-resources?)
                 profiles)))
