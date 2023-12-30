(ns ^:no-doc polylith.clj.core.workspace.settings
  (:require [polylith.clj.core.workspace.settings.test :as test]))

(defn enrich-settings [settings]
  (test/enrich-settings settings))
