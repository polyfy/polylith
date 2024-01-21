(ns ^:no-doc polylith.clj.core.info.table.ws-column.project-columns
  (:require [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.status :as status]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn alias-changes [[project changes] project->alias]
  [(project->alias project) (set changes)])

(defn status-flags [brick bricks-to-test path-entries is-show-resources]
  (str (status/brick-status-flags path-entries brick is-show-resources)
       (if (contains? (set bricks-to-test) brick) "x" "-")))

(defn project-cell [index column {:keys [name]} bricks-to-test path-entries is-show-resources]
  (let [statuses (status-flags name bricks-to-test path-entries is-show-resources)]
    (text-table/cell column (+ index 3) statuses :purple :center)))

(defn column [index {:keys [alias paths bricks-to-test]}
              bricks disk-paths is-show-loc is-show-resources thousand-separator]
  (let [column (+ 5 (* 2 index))
        path-entries (extract/from-paths paths disk-paths)
        bricks-in-project (set (select/names path-entries c/brick? c/exists?))
        total-loc-src (apply + (filter identity (map #(-> % :lines-of-code :src) (filter #(contains? bricks-in-project (:name %)) bricks))))]
    (concat
      [(text-table/cell column 1 alias :purple :center)]
      (map-indexed #(project-cell %1 column %2 bricks-to-test path-entries is-show-resources) bricks)
      (when is-show-loc [(text-table/number-cell column (+ 3 (count bricks)) total-loc-src :center thousand-separator)]))))

(defn columns [projects bricks disk-paths
               is-show-loc is-show-resources thousand-separator]
  (apply concat
         (map-indexed #(column %1 %2 bricks disk-paths is-show-loc is-show-resources thousand-separator)
                      projects)))
