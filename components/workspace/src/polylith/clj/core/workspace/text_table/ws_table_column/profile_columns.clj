(ns polylith.clj.core.workspace.text-table.ws-table-column.profile-columns
  (:require [polylith.clj.core.workspace.text-table.shared :as shared]
            [polylith.clj.core.path-finder.interfc :as path-finder]))

(defn status-flags [brick-name show-resources? path-entries]
  (path-finder/brick-status-flags path-entries brick-name show-resources?))

(defn profile-cell [index brick-name column show-resources? path-entries]
  (let [status (status-flags brick-name show-resources? path-entries)]
    (shared/standard-cell status column (+ index 3) :purple :center)))

(defn column [ws-dir index profile start-column settings bricks show-resources?]
  (let [column (+ start-column (* 2 index))
        path-entries (path-finder/profile-path-entries ws-dir settings profile)]
    (concat
      [(shared/header (name profile) column :purple :left)]
      (map-indexed #(profile-cell %1 %2 column show-resources? path-entries)
                   (map :name bricks)))))

(defn columns [ws-dir start-column bricks profiles settings show-resources?]
  (apply concat
    (map-indexed #(column ws-dir %1 %2 start-column settings bricks show-resources?)
                 profiles)))
