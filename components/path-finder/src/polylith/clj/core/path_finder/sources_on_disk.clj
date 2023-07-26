(ns ^:no-doc polylith.clj.core.path-finder.sources-on-disk
  (:require [polylith.clj.core.file.interface :as file]))

(defn- entity-source-paths [ws-dir entity-type entity]
  (eduction
    (filter file/not-hidden?)
    (map #(str entity-type "/" entity "/" %))
    (file/directories (str ws-dir "/" entity-type "/" entity))))

(defn- entity-paths [ws-dir entity-type]
  (eduction
    (mapcat #(entity-source-paths ws-dir entity-type %))
    (file/directories (str ws-dir "/" entity-type))))

(defn source-paths [ws-dir]
  (->> ["bases" "components" "projects"]
       (into [] (mapcat #(entity-paths ws-dir %)))
       (sort)
       (vec)))
