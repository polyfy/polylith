(ns polylith.clj.core.path-finder.interfc.extract
  (:require [polylith.clj.core.path-finder.path-extractor :as path-extractor]
            [polylith.clj.core.path-finder.lib-dep-extractor :as lib-dep-extractor]))

(defn path-entries [ws-dir [src-paths test-paths profile-src-paths profile-test-paths]]
   (path-extractor/path-entries ws-dir src-paths test-paths profile-src-paths profile-test-paths))

(defn from-unenriched-environment [ws-dir dev? src-paths test-paths settings user-input]
  (path-extractor/from-unenriched-environment ws-dir dev? src-paths test-paths settings user-input))

(defn from-profiles-paths [ws-dir settings profile-name]
  (path-extractor/from-profiles-paths ws-dir settings profile-name))

(defn from-library-deps [dev? src-deps test-deps settings user-input]
  (lib-dep-extractor/from-library-deps dev? src-deps test-deps settings user-input))
