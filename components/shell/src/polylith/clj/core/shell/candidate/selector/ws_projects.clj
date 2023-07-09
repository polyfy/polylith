(ns polylith.clj.core.shell.candidate.selector.ws-projects
  (:require [clojure.set :as set]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.shared :as shared]))

(defn select [{:keys [group]} groups {:keys [projects settings]}]
  (let [color-mode (:color-mode settings)
        project-names (set (map :name projects))]
    (mapv #(c/fn-explorer-child % :project color-mode group #'select)
          (sort (set/difference
                  project-names
                  (set (shared/args groups group)))))))
