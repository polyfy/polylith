(ns polylith.clj.core.deps.version
  (:import [org.eclipse.aether.util.version GenericVersionScheme]))

(defonce version-scheme (GenericVersionScheme.))

(defn version [ver]
  (.toString (.parseVersion ^GenericVersionScheme version-scheme ver)))

(defn latest
  "Return the latest Maven library version."
  [coord1 coord2]
  (let [v1 (version (:mvn/version coord1))
        v2 (version (:mvn/version coord2))]
    (if (< (compare v1 v2) 0)
      coord2 coord1)))
