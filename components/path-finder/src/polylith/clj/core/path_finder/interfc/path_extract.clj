(ns polylith.clj.core.path-finder.interfc.path-extract
  (:require [polylith.clj.core.path-finder.core :as core]
            [polylith.clj.core.path-finder.dep-extractor :as dep-extractor]))

(defn path-entries
  ([ws-dir src-paths]
   (core/path-entries ws-dir src-paths))
  ([ws-dir src-paths test-paths]
   (core/path-entries ws-dir src-paths test-paths))
  ([ws-dir src-paths test-paths profile-src-paths profile-test-paths]
   (core/path-entries ws-dir src-paths test-paths profile-src-paths profile-test-paths)))

(defn path-entries-including-settings [ws-dir dev? src-paths test-paths settings]
  (core/path-entries-from-settings ws-dir dev? src-paths test-paths settings))

(defn profile-path-entries [ws-dir settings profile-name]
  (core/profile-path-entries ws-dir settings profile-name))

(defn deps-entries [dev? src-deps test-deps settings]
  (dep-extractor/dep-entries dev? src-deps test-deps settings))
