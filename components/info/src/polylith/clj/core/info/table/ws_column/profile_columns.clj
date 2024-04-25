(ns ^:no-doc polylith.clj.core.info.table.ws-column.profile-columns
  (:require [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.info.table.ws-column.ws-brick :as ws-brick]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.status :as status]))

(defn status-flags [brick-name _ path-entries is-show-resources]
  (status/brick-status-flags path-entries brick-name is-show-resources))

(defn cell [index brick-name column is-show-resources path-entries]
  (let [flags (status/brick-status-flags path-entries brick-name is-show-resources)]
    (text-table/cell column (+ index 3) flags :purple :center)))

(defn column [index profile start-column disk-paths components bases ws-components ws-bases alias->workspace is-show-resources]
  (let [column (+ start-column (* 2 index))
        path-entries (extract/from-profiles-paths disk-paths profile)
        start-row1 (count components)
        start-row2 (+ start-row1 (count ws-components))
        start-row3 (+ start-row2 (count bases))]
    (concat
      [(text-table/cell column 1 (:name profile) :purple :left)]
      (map-indexed #(cell %1 (:name %2) column is-show-resources path-entries) components)
      (map-indexed #(ws-brick/cell %1 start-row1 column %2 [] alias->workspace is-show-resources status-flags) ws-components)
      (map-indexed #(cell (+ %1 start-row2) (:name %2) column is-show-resources path-entries) bases)
      (map-indexed #(ws-brick/cell %1 start-row3 column %2 [] alias->workspace is-show-resources status-flags) ws-bases))))

(defn columns [start-column
               components
               bases
               profiles
               ws-components
               ws-bases
               alias->workspace
               disk-paths
               is-show-resources]
  (apply concat
         (map-indexed #(column %1 %2 start-column disk-paths components bases ws-components ws-bases alias->workspace is-show-resources)
                      profiles)))
