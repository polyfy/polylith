(ns polylith.clj.core.entity.dep-selector
  (:require [polylith.clj.core.entity.matchers :as m]))

(defn select-deps [dep-entries]
  (into {} (map :dep dep-entries)))

(defn all-src-deps [dep-entries]
  (select-deps (m/filter-entries dep-entries [m/=src])))

(defn all-test-deps [dep-entries]
  (select-deps (m/filter-entries dep-entries [m/=test])))
