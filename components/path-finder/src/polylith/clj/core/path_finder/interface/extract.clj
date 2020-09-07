(ns polylith.clj.core.path-finder.interface.extract
  (:require [polylith.clj.core.path-finder.path-extractor :as path-extractor]
            [polylith.clj.core.path-finder.lib-dep-extractor :as lib-dep-extractor]))

(defn path-entries [[src-paths test-paths profile-src-paths profile-test-paths] disk-paths]
  (path-extractor/path-entries src-paths test-paths profile-src-paths profile-test-paths disk-paths))

(defn from-unenriched-environment [dev? src-paths test-paths disk-paths settings user-input]
  (path-extractor/from-unenriched-environment dev? src-paths test-paths disk-paths settings user-input))

(defn from-profiles-paths [disk-paths settings profile-name]
  (path-extractor/from-profiles-paths disk-paths settings profile-name))

(defn from-library-deps [dev? src-deps test-deps settings user-input]
  (lib-dep-extractor/from-library-deps dev? src-deps test-deps settings user-input))
