(ns polylith.util.core)

(defn ordered-map [keyvals]
  "Takes a key/valus and returns an ordered map,
   execept entries that has nil as a value"
  (let [pairs (filter #(-> % second nil? not)
                      (partition 2 keyvals))]
    (apply array-map
           (mapcat identity pairs))))
