(ns polylith.clj.core.util.interfc.params
  (:require [clojure.string :as str]))

(defn named? [arg]
  (and (-> arg nil? not)
       (str/includes? arg ":")))

(defn unnamed? [arg]
  (and (-> arg nil? not)
       (-> arg named? not)))

(defn key-name [arg]
  (let [index (str/index-of arg ":")]
    [(keyword (subs arg 0 index))
     (subs arg (inc index))]))

(defn parse [& params]
  (let [unnamed-args (filterv unnamed? params)
        named-args (into {} (map key-name)
                         (filterv named? params))]
    {:named-args named-args
     :unnamed-args unnamed-args}))
