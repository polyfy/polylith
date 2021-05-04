(ns polylith.clj.core.util.core)

(defn find-first [predicate sequence]
  (first (filter predicate sequence)))

(defn ordered-map
  "Takes a vector of key/value pairs and returns
   an ordered map, except entries that has nil as a value"
  [keyvals]
  (let [pairs (filter #(-> % second nil? not)
                      (partition 2 keyvals))]
    (apply array-map
           (mapcat identity pairs))))

(defn- key-as-string [[lib version]]
  [(str lib) version])

(defn stringify-and-sort-map [m]
  (apply array-map
         (mapcat identity
                 (sort (map key-as-string m)))))

(defn- def-val [amap key]
  (list 'def key (list (keyword key) amap)))

(defmacro def-map [amap keys]
  (conj (map #(def-val amap %) keys) 'do))
