(ns polylith.change.core
  (:require [clojure.string :as str]
            [polylith.git.interface :as git]))

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
  {:bases (set (filter identity (map base filenames)))
   :components (set (filter identity (map component filenames)))})

(defn changes
  ([]
   (let [filenames (git/diff)]
     (bricks filenames)))
  ([hash1]
   (let [filenames (git/diff hash1)]
     (bricks filenames)))
  ([hash1 hash2]
   (let [filenames (git/diff hash1 hash2)]
     (bricks filenames))))
