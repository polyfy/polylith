(ns ^:no-doc polylith.clj.core.maven.core
  (:import [org.eclipse.aether.util.version GenericVersionScheme]))

(defonce version-scheme (GenericVersionScheme.))

(defn version [ver]
  (when ver
    (.toString (.parseVersion ^GenericVersionScheme version-scheme ver))))

(defn latest-lib
  "Return the latest Maven library version."
  [coord1 coord2 mvn-key]
  (let [v1 (version (mvn-key coord1))
        v2 (version (mvn-key coord2))]
    (if (and v1 v2)
      (if (< (compare v1 v2) 0)
        coord2 coord1)
      (if v1 coord1 coord2))))

(defn oldest-lib
  "Return the oldest Maven library version."
  [coord1 coord2 mvn-key]
  (let [v1 (version (mvn-key coord1))
        v2 (version (mvn-key coord2))]
    (if (and v1 v2)
      (if (< (compare v1 v2) 0)
        coord1 coord2)
      (if v1 coord1 coord2))))
