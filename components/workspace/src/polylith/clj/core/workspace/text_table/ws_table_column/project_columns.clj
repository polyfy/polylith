(ns polylith.clj.core.workspace.text-table.ws-table-column.project-columns
  (:require [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.status :as status]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn alias-changes [[project changes] project->alias]
  [(project->alias project) (set changes)])

(defn status-flags [alias brick alias->bricks-to-test path-entries is-show-resources]
  (str (status/brick-status-flags path-entries brick is-show-resources)
       (if (contains? (alias->bricks-to-test alias) brick) "x" "-")))

(defn project-cell [index column {:keys [name]} alias alias->bricks-to-test path-entries is-show-resources]
  (let [statuses (status-flags alias name alias->bricks-to-test path-entries is-show-resources)]
    (text-table/cell column (+ index 3) statuses :purple :center)))

(defn column [index {:keys [alias src-paths test-paths profile-src-paths profile-test-paths]}
              bricks disk-paths alias->bricks-to-test is-show-loc is-show-resources thousand-sep]
  (let [column (+ 5 (* 2 index))
        path-entries (extract/path-entries [src-paths test-paths profile-src-paths profile-test-paths] disk-paths)
        bricks-in-project (set (select/names path-entries c/brick? c/src? c/exists?))
        total-loc-src (apply + (filter identity (map :lines-of-code-src (filter #(contains? bricks-in-project (:name %)) bricks))))]
    (concat
      [(text-table/cell column 1 alias :purple :center)]
      (map-indexed #(project-cell %1 column %2 alias alias->bricks-to-test path-entries is-show-resources) bricks)
      (when is-show-loc [(text-table/number-cell column (+ 3 (count bricks)) total-loc-src :center thousand-sep)]))))

(defn columns [projects bricks disk-paths {:keys [project-to-bricks-to-test]}
               is-show-loc is-show-resources thousand-sep]
  (let [project->alias (into {} (map (juxt :name :alias) projects))
        alias->bricks-to-test (into {} (map #(alias-changes % project->alias) project-to-bricks-to-test))]
    (apply concat
           (map-indexed #(column %1 %2 bricks disk-paths alias->bricks-to-test is-show-loc is-show-resources thousand-sep)
                        projects))))
