(ns polylith.core.circulardeps
  (:require [clojure.string :as str]))

(defn interface-deps [brick]
  [(:name brick)
   (-> brick :dependencies :interfaces set)])

(defn interface-circular-deps [interface completed-deps interface->deps path]
  (if (contains? completed-deps interface)
    {:error (conj path interface)}
    (mapcat #(interface-circular-deps % (conj completed-deps interface) interface->deps (conj path interface))
            (interface->deps interface))))

(defn component-circular-deps [interface components]
  (let [interface->deps (into {} (map interface-deps components))]
    (-> (interface-circular-deps interface #{} interface->deps [])
        first second)))

(defn circular-deps [interfaces components]
  (first (sort-by count
                  (filter identity
                          (map #(component-circular-deps % components)
                               interfaces)))))
