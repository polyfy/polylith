(ns ^:no-doc polylith.clj.core.maven.core
  (:require [clojure.string :as str]))

(defn values [val]
  (try
    (Long/parseLong val)
    (catch NumberFormatException _
      val)))

(defn as-vals [version]
  (when version
    (mapv values (str/split (str version) #"\."))))

(defn sort-libs [coord1 coord2 mvn-key]
  (let [v1 (-> coord1 mvn-key as-vals)
        v2 (-> coord2 mvn-key as-vals)]
    (if (and v1 v2)
      (if (< (compare v1 v2) 0)
        [coord1 coord2]
        [coord2 coord1])
      (if v1
        [coord1 coord2]
        [coord2 coord1]))))

(defn latest-lib
  "Return the latest Maven library version."
  [coord1 coord2 mvn-key]
  (second (sort-libs coord1 coord2 mvn-key)))

(defn oldest-lib
  "Return the oldest Maven library version."
  [coord1 coord2 mvn-key]
  (first (sort-libs coord1 coord2 mvn-key)))
