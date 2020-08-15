(ns polylith.clj.core.change.entity
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interfc.paths :as entity]
            [polylith.clj.core.git.interfc :as git]))

(defn environment? [filename]
  (str/starts-with? filename "environments/"))

(defn extract-entity [filename brick? length]
  (when (brick? filename)
    (let [path (subs filename length)
          index (str/index-of path "/")]
      (subs path 0 index))))

(defn environment [filename]
  (extract-entity filename environment? 13))

(defn changed-entities [paths]
  "Returns the bricks and environments that has changed based on a list of files"
  {:changed-bases (entity/bases-from-paths paths)
   :changed-components (entity/components-from-paths paths)
   :changed-environments (entity/environments-from-paths paths)})

(defn changes [ws-dir sha1 sha2]
  (let [filenames (git/diff ws-dir sha1 sha2)]
    (changed-entities filenames)))
