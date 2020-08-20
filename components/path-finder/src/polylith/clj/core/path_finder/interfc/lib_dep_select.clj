(ns polylith.clj.core.path-finder.interfc.lib-dep-select
  (:require [polylith.clj.core.path-finder.dep-selector :as dep-selector]))

(defn all-src-deps [dep-entries]
  (dep-selector/all-src-deps dep-entries))

(defn all-test-deps [dep-entries]
  (dep-selector/all-test-deps dep-entries))
