(ns ^:no-doc polylith.clj.core.nav-generator.help-generator
  (:require [clojure.string :as str]))

(defn command [bookmark]
  (let [the-rest (subs bookmark 2)
        cmd (subs the-rest 0 (-> the-rest count dec))]
    [cmd []]))

(defn navigation []
  (into (sorted-map)
        (mapv command
              (filter #(str/starts-with? % "[#")
                      (-> "doc/commands.adoc"
                          slurp
                          str/split-lines)))))
