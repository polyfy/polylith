(ns ^:no-doc polylith.clj.core.nav-generator.ws-generator
  (:require [clojure.string :as str]))

(defn ws-key [bookmark]
  [(subs bookmark 3) []])

(defn navigation []
  (into (sorted-map)
        (mapv ws-key
              (filter #(str/starts-with? % "== ")
                      (-> "doc/workspace-structure.adoc"
                          slurp
                          str/split-lines)))))
