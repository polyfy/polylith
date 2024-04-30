(ns ^:no-doc polylith.clj.core.info.table.ws-column.profile-columns
  (:require [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.status :as status]))

(defn status-flags [brick-name _ path-entries is-show-resources]
  (status/brick-status-flags path-entries brick-name is-show-resources))

(defn ->path-entries [alias path-entries profile-path-entries]
  (if alias
    (filter #(= alias (:alias %))
            profile-path-entries)
    path-entries))

(defn cell [index {:keys [alias name]} column is-show-resources path-entries profile-path-entries]
  (let [path-entries (->path-entries alias path-entries profile-path-entries)
        flags (status/brick-status-flags path-entries name is-show-resources)]
    (text-table/cell column (+ index 3) flags :purple :center)))

(defn column [index profile start-column disk-paths bricks alias->workspace is-show-resources]
  (let [column (+ start-column (* 2 index))
        path-entries (extract/from-profiles-paths disk-paths profile)
        profile-path-entries (extract/profile-path-entries profile alias->workspace)]
    (concat
      [(text-table/cell column 1 (:name profile) :purple :left)]
      (map-indexed #(cell %1 %2 column is-show-resources path-entries profile-path-entries)
                   bricks))))

(defn columns [start-column
               profiles
               bricks
               alias->workspace
               disk-paths
               is-show-resources]
  (apply concat
         (map-indexed #(column %1 %2 start-column disk-paths bricks alias->workspace is-show-resources)
                      profiles)))
