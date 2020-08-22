(ns polylith.clj.core.change.entity
  (:require [polylith.clj.core.git.interfc :as git]
            [polylith.clj.core.path-finder.interfc.criterias :as c]
            [polylith.clj.core.path-finder.interfc.select :as select]
            [polylith.clj.core.path-finder.interfc.extract :as extract]))

(defn changed-entities [ws-dir paths]
  "Returns the bricks and environments that has changed based on a list of files"
  (let [path-entries (extract/path-entries ws-dir [paths])]
    {:changed-bases (select/names path-entries c/base? c/src? c/exists?)
     :changed-components (select/names path-entries c/component? c/src? c/exists?)
     :changed-environments (select/names path-entries c/environment? c/src? c/exists?)}))

(defn changes [ws-dir sha1 sha2]
  (let [filenames (git/diff ws-dir sha1 sha2)]
    (changed-entities ws-dir filenames)))
