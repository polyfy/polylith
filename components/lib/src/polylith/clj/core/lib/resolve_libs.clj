(ns polylith.clj.core.lib.resolve-libs
  (:require [polylith.clj.core.lib.maven-dep :as maven-dep]))

(defn latest [m [lib version]]
  (assoc m lib (maven-dep/latest (m lib) version)))

(defn resolve-libs [src-deps override-deps]
  (merge (reduce latest {} src-deps)
         override-deps))
