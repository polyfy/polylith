(ns polylith.clj.core.path-finder.interfc.path-status
  (:require [polylith.clj.core.path-finder.status :as status]))

(defn brick-status-flags [path-entries brick-name show-resources?]
  (status/status-flags path-entries :brick brick-name show-resources?))

(defn env-status-flags [path-entries env-name show-resources?]
  (status/status-flags path-entries :env env-name show-resources?))
