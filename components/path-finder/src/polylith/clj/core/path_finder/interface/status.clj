(ns ^:no-doc polylith.clj.core.path-finder.interface.status
  (:require [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.status-calculator :as status-calculator]))

(defn brick-status-flags [path-entries brick-name is-show-resources]
  (status-calculator/status-flags path-entries brick-name is-show-resources c/brick? c/truthy))

(defn project-status-flags [path-entries project-name is-show-resources]
  (status-calculator/status-flags path-entries project-name is-show-resources c/project? c/test-path?))
