(ns ^:no-doc polylith.clj.core.lib.resolve-libs
  (:require [polylith.clj.core.maven.interface :as maven]))

(defn latest [m [library version]]
  (if (contains? m library)
    (if (= "maven" (get-in m [library :type]))
      (assoc m library (maven/latest-lib (get m library) version :version))
      m)
    (assoc m library version)))

(defn resolve-libs [src-deps override-deps]
  (merge (reduce latest {} src-deps)
         override-deps))
