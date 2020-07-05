(ns polylith.clj.core.change.brick
  (:require [clojure.string :as str]
            [polylith.clj.core.git.interfc :as git]))

(defn base? [filename]
  (str/starts-with? filename "bases/"))

(defn component? [filename]
  (str/starts-with? filename "components/"))

(defn extract-brick [filename brick? length]
  (when (brick? filename)
    (let [path (subs filename length)
          index (str/index-of path "/")]
      (subs path 0 index))))

(defn base [filename]
  (extract-brick filename base? 6))

(defn component [filename]
  (extract-brick filename component? 11))

(defn bricks [filenames]
  "Returns the bricks that has changed based on a list of files"
  {:bases (vec (sort (set (filter identity (map base filenames)))))
   :components (vec (sort (set (filter identity (map component filenames)))))})

(defn changes [sha1 sha2]
  (let [filenames (git/diff sha1 sha2)]
    (bricks filenames)))
