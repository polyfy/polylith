(ns ^:no-doc polylith.clj.core.doc.generator.ws
  (:require [clojure.string :as str]))

(defn ws-key [bookmark]
  [(subs bookmark 3) []])

(comment
  ;; ws-pages
  (into (sorted-map)
        (mapv ws-key
              (filter #(str/starts-with? % "== ")
                      (-> "doc/workspace-structure.adoc"
                          slurp
                          str/split-lines))))
  #__)
