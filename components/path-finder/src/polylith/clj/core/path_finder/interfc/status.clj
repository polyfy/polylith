(ns polylith.clj.core.path-finder.interfc.status
  (:require [polylith.clj.core.path-finder.status-calculator :as status-calculator]))

(defn brick-status-flags [path-entries brick-name show-resources?]
  (status-calculator/status-flags path-entries :brick brick-name show-resources?))

(defn env-status-flags [path-entries env-name show-resources?]
  (status-calculator/status-flags path-entries :env env-name show-resources?))
