(ns polylith.clj.core.change.entity
  (:require [polylith.clj.core.git.interfc :as git]
            [polylith.clj.core.path-finder.interfc :as path-finder]))

(defn changed-entities [ws-dir paths]
  "Returns the bricks and environments that has changed based on a list of files"
  (let [path-entries (path-finder/path-entries ws-dir paths)]
    {:changed-bases (path-finder/src-base-names path-entries)
     :changed-components (path-finder/src-component-names path-entries)
     :changed-environments (path-finder/src-environment-names path-entries)}))

(defn changes [ws-dir sha1 sha2]
  (let [filenames (git/diff ws-dir sha1 sha2)]
    (changed-entities ws-dir filenames)))
