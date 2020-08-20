(ns polylith.clj.core.path-finder.interfc.path-extract
  (:require [polylith.clj.core.path-finder.core :as core]
            [polylith.clj.core.path-finder.dep-extractor :as dep-extractor]))

(defn path-entries
  ([ws-dir settings]
   (core/path-entries ws-dir settings))
  ([ws-dir settings profile-name]
   (core/path-entries ws-dir settings profile-name))
  ([ws-dir dev? src-paths test-paths settings]
   (core/path-entries ws-dir dev? src-paths test-paths settings)))

(defn deps-entries [dev? src-deps test-deps settings]
  (dep-extractor/dep-entries dev? src-deps test-deps settings))
