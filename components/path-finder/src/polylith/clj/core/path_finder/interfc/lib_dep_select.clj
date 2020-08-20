(ns polylith.clj.core.path-finder.interfc.lib-dep-select
  (:require [polylith.clj.core.path-finder.matchers :as m]
            [polylith.clj.core.path-finder.dep-selector :as dep-selector]))

(defn deps [dep-entries & criterias]
  (into {} (map :dep
                (m/filter-entries dep-entries criterias))))
