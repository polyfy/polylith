(ns ^:no-doc polylith.clj.core.doc.generator.command
  (:require [clojure.string :as str]))

(defn command [bookmark]
  (let [the-rest (subs bookmark 2)
        cmd (subs the-rest 0 (-> the-rest count dec))]
    [cmd []]))

(comment
  ;; commands
  (into (sorted-map)
        (mapv command
              (filter #(str/starts-with? % "[#")
                      (-> "doc/commands.adoc"
                          slurp
                          str/split-lines))))
  #__)
