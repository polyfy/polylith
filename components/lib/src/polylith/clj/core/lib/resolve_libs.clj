(ns polylith.clj.core.lib.resolve-libs
  (:require [polylith.clj.core.lib.maven-dep :as maven-dep]))

(defn latest [m [library version]]
  (if (contains? m library)
    (if (= "maven" (get-in m [library :type]))
      (assoc m library (maven-dep/latest (get m library) version :version))
      m)
    (assoc m library version)))

(defn resolve-libs [src-deps override-deps]
  (merge (reduce latest {} src-deps)
         override-deps))
