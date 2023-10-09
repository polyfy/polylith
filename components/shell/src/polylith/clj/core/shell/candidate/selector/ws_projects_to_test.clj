(ns ^:no-doc polylith.clj.core.shell.candidate.selector.ws-projects-to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.shared :as shared]))

(defn select
  "The idea with the project:P1:P2 argument is to select which projects
   to test, and that's why we only return projects that are marked for
   testing (we don't need to filter out already filtered out projects)."
  [{:keys [group]} groups {:keys [settings changes]}]
  (let [color-mode (:color-mode settings)
        {:keys [project-to-bricks-to-test
                project-to-projects-to-test]} changes]
    (mapv #(c/fn-explorer-child % :project color-mode true group #'select)
          (sort (set/difference
                  (set (concat ["development"]
                               (map first (filter #(-> % second seq) project-to-bricks-to-test))
                               (map first (filter #(-> % second seq) project-to-projects-to-test))))
                  (set (shared/args groups group)))))))
