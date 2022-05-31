(ns polylith.clj.core.path-finder.sources-on-disk
  (:require [polylith.clj.core.file.interface :as file]))

(defn entity-source-paths [ws-dir entity-type entity]
  (map #(str entity-type "/" entity "/" %)
    (filter file/not-hidden?
            (file/directories (str ws-dir "/" entity-type "/" entity)))))

(defn entity-paths [ws-dir entity-type]
  (mapcat #(entity-source-paths ws-dir entity-type %)
          (file/directories (str ws-dir "/" entity-type))))

(defn source-paths [ws-dir]
  (vec (sort (mapcat #(entity-paths ws-dir %)
                     ["bases" "components" "projects"]))))
