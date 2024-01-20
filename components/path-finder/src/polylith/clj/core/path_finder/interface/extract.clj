(ns ^:no-doc polylith.clj.core.path-finder.interface.extract
  (:require [polylith.clj.core.path-finder.path-extractor :as path-extractor]
            [polylith.clj.core.path-finder.lib-dep-extractor :as lib-dep-extractor]))

(defn from-paths [paths disk-paths]
  (path-extractor/from-paths paths disk-paths))

(defn from-unenriched-project [is-dev paths disk-paths profiles settings]
  (path-extractor/from-unenriched-project is-dev paths disk-paths profiles settings))

(defn from-profiles-paths [disk-paths profile]
  (path-extractor/from-profiles-paths disk-paths profile))

(defn from-library-deps [is-dev deps profiles settings]
  (lib-dep-extractor/from-library-deps is-dev deps profiles settings))
