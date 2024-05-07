(ns ^:no-doc polylith.clj.core.info.table.ws-column.project-columns
  (:require [polylith.clj.core.info.table.ws-column.external.cell :as ext-cell]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.status :as status]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn status-flags [brick-name bricks-to-test path-entries is-show-resources]
  (str (status/brick-status-flags path-entries brick-name is-show-resources)
       (if (contains? (set bricks-to-test) brick-name) "x" "-")))

(defn cell [index start-row column {:keys [name]} bricks-to-test path-entries is-show-resources]
  (let [statuses (status-flags name bricks-to-test path-entries is-show-resources)]
    (text-table/cell column (+ index start-row 3) statuses :purple :center)))

(defn column [index {:keys [alias paths bricks-to-test]}
              components bases disk-paths
              ws-components ws-bases alias->workspace
              is-show-loc is-show-resources thousand-separator]
  (let [column (+ 5 (* 2 index))
        bricks (concat components bases)
        start-row1 (count components)
        start-row2 (+ start-row1 (count ws-components))
        start-row3 (+ start-row2 (count bases))
        start-row4 (+ 3 start-row3 (count ws-bases))
        path-entries (extract/from-paths paths disk-paths)
        bricks-in-project (set (select/names path-entries c/brick? c/exists?))
        total-loc-src (apply + (filter identity (map #(-> % :lines-of-code :src) (filter #(contains? bricks-in-project (:name %)) bricks))))]
    (concat
      [(text-table/cell column 1 alias :purple :center)]
      (map-indexed #(cell %1 0 column %2 bricks-to-test path-entries is-show-resources) components)
      (map-indexed #(ext-cell/cell %1 start-row1 column %2 [] alias->workspace is-show-resources status-flags) ws-components)
      (map-indexed #(cell %1 start-row2 column %2 bricks-to-test path-entries is-show-resources) bases)
      (map-indexed #(ext-cell/cell %1 start-row3 column %2 [] alias->workspace is-show-resources status-flags) ws-bases)
      (when is-show-loc [(text-table/number-cell column start-row4 total-loc-src :center thousand-separator)]))))

(defn columns [projects components bases disk-paths
               ws-components ws-bases alias->workspace
               is-show-loc is-show-resources thousand-separator]
  (apply concat
         (map-indexed #(column %1 %2 components bases disk-paths ws-components ws-bases alias->workspace is-show-loc is-show-resources thousand-separator)
                      projects)))
