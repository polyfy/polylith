(ns polylith.clj.core.util.interfc.params
  (:require [clojure.string :as str]))

(defn named? [arg]
  (and (-> arg nil? not)
       (str/includes? arg ":")))

(defn unnamed? [arg]
  (and (-> arg nil? not)
       (-> arg named? not)))

(defn key-name [arg]
  (let [parts (str/split arg #":")]
    (if (= "" (first parts))
      [(keyword (second parts))
       "true"]
      (if (= 2 (count parts))
        [(-> parts first keyword)
         (second parts)]
        [(-> parts first keyword)
         (vec (drop 1 parts))]))))

(defn extract [args]
  (let [unnamed-args (filterv unnamed? args)
        named-args (into {} (map key-name
                                 (filterv named? args)))]
    {:named-args named-args
     :unnamed-args unnamed-args}))
