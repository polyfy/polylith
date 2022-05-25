(ns polylith.clj.core.change.projects-to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.extract :as extract]))

(defn included-projects [paths disk-paths is-dev]
  (let [path-entries (extract/from-paths paths disk-paths)
        project-names (set (select/names path-entries c/project? c/test-path? c/exists?))]
    (if is-dev
      project-names
      (set/difference project-names #{"development"}))))

(defn run-tests? [project-alias project-name is-dev is-dev-user-input is-run-project-tests selected-projects]
  (and is-run-project-tests
       (or (or (contains? selected-projects project-name)
               (contains? selected-projects project-alias))
           (and (empty? selected-projects)
                (or (not is-dev)
                    is-dev-user-input)))))

(defn projects-to-test [{:keys [is-dev alias name paths]} disk-paths affected-projects selected-projects is-dev-user-input is-run-project-tests is-all]
  (let [run-tests? (run-tests? alias name is-dev is-dev-user-input is-run-project-tests selected-projects)
        included-projects (included-projects paths disk-paths is-dev)]
    [name
     (cond-> []
       run-tests?
       (into (cond
               is-all (sort included-projects)
               is-run-project-tests (sort (set/intersection (set affected-projects)
                                                            (set included-projects))))))]))

(defn project-to-projects-to-test [projects disk-paths affected-projects selected-projects is-dev-user-input is-run-project-tests is-all]
  (into {}
        (map #(projects-to-test % disk-paths affected-projects selected-projects is-dev-user-input is-run-project-tests is-all))
        projects))
