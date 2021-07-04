(ns polylith.clj.core.lib.resolve-libs
  (:require [polylith.clj.core.lib.maven-dep :as maven-dep]))

(defn latest [m [library version]]
  (let [lib (str library)]
    (if (= "maven" (get-in m [lib :type]))
      (assoc-in m
                [lib :version]
                (:mvn/version
                  (maven-dep/latest {:mvn/version (get-in m [lib :version])}
                                    version)))
      m)))

(defn resolve-libs [src-deps override-deps]
  (reduce latest (into {} src-deps) override-deps))
