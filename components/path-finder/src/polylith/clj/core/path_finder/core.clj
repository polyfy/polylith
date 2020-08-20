(ns polylith.clj.core.path-finder.core
  (:require [polylith.clj.core.path-finder.path-extractor :as path-extractor]
            [polylith.clj.core.path-finder.profile-src-splitter :as profile-src-splitter]))

(defn entries [ws-dir src-paths test-paths profile-src-paths profile-test-paths]
  (vec (concat (path-extractor/path-entries ws-dir src-paths false false)
               (path-extractor/path-entries ws-dir test-paths false true)
               (path-extractor/path-entries ws-dir profile-src-paths true false)
               (path-extractor/path-entries ws-dir profile-test-paths true true))))

(defn path-entries
  ([ws-dir {:keys [src-paths
                   test-paths
                   profile-src-paths
                   profile-test-paths]}]
   (entries ws-dir src-paths test-paths profile-src-paths profile-test-paths))
  ([ws-dir settings profile-name]
   (let [{:keys [src-paths test-paths]} (profile-src-splitter/extract-profile-paths profile-name settings)]
     (entries ws-dir src-paths test-paths nil nil)))
  ([ws-dir dev? src-paths test-paths settings]
   (let [{:keys [profile-src-paths profile-test-paths]} (profile-src-splitter/extract-active-dev-profiles-paths dev? settings)]
     (entries ws-dir src-paths test-paths profile-src-paths profile-test-paths))))
