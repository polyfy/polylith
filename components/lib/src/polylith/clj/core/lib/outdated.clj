(ns polylith.clj.core.lib.outdated
  (:require [polylith.clj.core.antq.ifc :as antq]))

(defn outdated-libs [{:keys [configs]}]
  (set (map #(-> % ffirst)
            (antq/library->latest-version configs))))
