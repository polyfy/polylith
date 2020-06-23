(ns polylith.util.core)

(defn find-first [predicate sequence]
  (first (filter predicate sequence)))

(defn ordered-map [keyvals]
  "Takes a vector of key/value pairs and returns
   an ordered map, except entries that has nil as a value"
  (let [pairs (filter #(-> % second nil? not)
                      (partition 2 keyvals))]
    (apply array-map
           (mapcat identity pairs))))
