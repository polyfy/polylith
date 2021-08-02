(ns polylith.clj.core.change.projects-to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.extract :as extract]))

(defn included-projects [{:keys [paths]} disk-paths]
  (let [path-entries (extract/from-paths paths disk-paths)]
    (select/names path-entries c/project? c/test-path? c/exists?)))

(defn select-projects [project-name projects is-dev]
  (if is-dev
    projects
    (if (= "development" project-name)
      []
      (set/difference (set projects) #{"development"}))))

(defn project-tests [project-name affected-projects included-projects is-dev]
  (let [projects (set/intersection (set affected-projects)
                                   (set included-projects))]
    (select-projects project-name projects is-dev)))

(defn projects-to-test [{:keys [name is-run-tests] :as project} disk-paths affected-projects is-dev is-run-project-tests is-all]
  (let [included-projects (included-projects project disk-paths)]
    (cond
      is-all [name (vec (sort (select-projects name included-projects is-dev)))]
      (and is-run-tests is-run-project-tests) [name (vec (sort (project-tests name affected-projects included-projects is-dev)))]
      :else [name []])))

(defn project-to-projects-to-test [projects affected-projects disk-paths is-dev is-run-project-tests is-all]
  (into {} (map #(projects-to-test % disk-paths affected-projects is-dev is-run-project-tests is-all)
                projects)))
