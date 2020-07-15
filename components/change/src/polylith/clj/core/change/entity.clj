(ns polylith.clj.core.change.entity
  (:require [clojure.string :as str]
            [polylith.clj.core.git.interfc :as git]))

(defn base? [filename]
  (str/starts-with? filename "bases/"))

(defn component? [filename]
  (str/starts-with? filename "components/"))

(defn environment? [filename]
  (str/starts-with? filename "environments/"))

(defn extract-entity [filename brick? length]
  (when (brick? filename)
    (let [path (subs filename length)
          index (str/index-of path "/")]
      (subs path 0 index))))

(defn base [filename]
  (extract-entity filename base? 6))

(defn component [filename]
  (extract-entity filename component? 11))

(defn environment [filename]
  (extract-entity filename environment? 13))

(defn changed-entities [filenames]
  "Returns the bricks and environments that has changed based on a list of files"
  {:changed-bases (vec (sort (set (filter identity (map base filenames)))))
   :changed-components (vec (sort (set (filter identity (map component filenames)))))
   :changed-environments (vec (sort (set (filter identity (map environment filenames)))))})

(defn changes [sha1 sha2]
  (let [filenames (git/diff sha1 sha2)]
    (changed-entities filenames)))
