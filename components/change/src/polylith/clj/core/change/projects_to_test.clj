(ns polylith.clj.core.change.projects-to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.extract :as extract]))

(defn existing-projects [{:keys [paths]} disk-paths]
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

;; todo: fix
;(defn is-run-tests []
;
;  (or (and (not is-dev)
;           (or is-run-all-brick-tests
;               (empty? selected-projects)))
;      (or (contains? selected-projects project-name)
;          (contains? selected-projects alias))))


(defn projects-to-test [{:keys [is-dev name] :as project} disk-paths affected-projects selected-projects is-dev-user-input is-run-project-tests is-all]
  (let [is-run-tests true ;; todo: fix
        existing-projects (existing-projects project disk-paths)]
    (cond
      is-all [name (vec (sort (select-projects name existing-projects is-dev-user-input)))]
      (and is-run-tests is-run-project-tests) [name (vec (sort (project-tests name affected-projects existing-projects is-dev)))]
      :else [name []])))

(defn project-to-projects-to-test [projects disk-paths affected-projects selected-projects is-dev-user-input is-run-project-tests is-all]
  (into {} (map #(projects-to-test % disk-paths affected-projects selected-projects is-dev-user-input is-run-project-tests is-all)
                projects)))
