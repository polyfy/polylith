(ns polylith.clj.core.path-finder.interface.extract
  (:require [polylith.clj.core.path-finder.path-extractor :as path-extractor]
            [polylith.clj.core.path-finder.lib-dep-extractor :as lib-dep-extractor]))

(defn from-paths [paths disk-paths]
  (path-extractor/from-paths paths disk-paths))

(defn from-unenriched-project [is-dev paths disk-paths settings]
  (path-extractor/from-unenriched-project is-dev paths disk-paths settings))

(defn from-profiles-paths [disk-paths settings profile-name]
  (path-extractor/from-profiles-paths disk-paths settings profile-name))

(defn from-library-deps [is-dev deps settings]
  (lib-dep-extractor/from-library-deps is-dev deps settings))
