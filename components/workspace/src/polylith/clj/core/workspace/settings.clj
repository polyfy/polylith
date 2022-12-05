(ns polylith.clj.core.workspace.settings
  (:require [polylith.clj.core.workspace.settings.alias :as alias]
            [polylith.clj.core.workspace.settings.test :as test]))

(defn enrich-settings [settings projects]
  (-> settings
      (alias/enrich-settings projects)
      (test/enrich-settings)))
