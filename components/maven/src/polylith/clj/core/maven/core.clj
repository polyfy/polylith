(ns ^:no-doc polylith.clj.core.maven.core
  (:import [org.apache.maven.artifact.versioning ComparableVersion]))

(defn sort-libs [coord1 coord2 mvn-key]
  (let [v1 (-> coord1 mvn-key ComparableVersion.)
        v2 (-> coord2 mvn-key ComparableVersion.)]
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
