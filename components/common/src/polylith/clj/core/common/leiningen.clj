(ns polylith.clj.core.common.leiningen
  (:require [polylith.clj.core.file.interface :as file]))

(defn config-key
  "Find and return a key in a Leiningen config file."
  [config-path key]
  (let [content (first (file/read-file (str config-path "/project.clj")))
        index (ffirst
                (filter #(= key (second %))
                        (map-indexed vector content)))]
    (when index (nth content (inc index)))))
