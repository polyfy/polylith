(ns polylith.clj.core.path-finder.interfc.path-extract
  (:require [polylith.clj.core.path-finder.path-extractor :as path-extractor]
            [polylith.clj.core.path-finder.profile-src-splitter :as profile-src-splitter]))

(defn path-entries
  ([ws-dir {:keys [src-paths
                   test-paths
                   profile-src-paths
                   profile-test-paths]}]
   (path-extractor/path-entries ws-dir src-paths test-paths profile-src-paths profile-test-paths))
  ([ws-dir settings profile-name]
   (let [{:keys [src-paths test-paths]} (profile-src-splitter/extract-profile-paths profile-name settings)]
     (path-extractor/path-entries ws-dir src-paths test-paths nil nil)))
  ([ws-dir dev? src-paths test-paths settings]
   (let [{:keys [profile-src-paths profile-test-paths]} (profile-src-splitter/extract-active-dev-profiles-paths dev? settings)]
     (path-extractor/path-entries ws-dir src-paths test-paths profile-src-paths profile-test-paths))))
