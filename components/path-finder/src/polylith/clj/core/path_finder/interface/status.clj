(ns polylith.clj.core.path-finder.interface.status
  (:require [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.status-calculator :as status-calculator]))

(defn brick-status-flags [path-entries brick-name is-show-resources]
  (status-calculator/status-flags path-entries c/brick? brick-name is-show-resources))

(defn env-status-flags [path-entries env-name is-show-resources]
  (status-calculator/status-flags path-entries c/environment? env-name is-show-resources))
