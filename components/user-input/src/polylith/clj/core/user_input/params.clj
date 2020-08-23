(ns polylith.clj.core.user-input.params
  (:require [clojure.string :as str]))

(defn named? [arg]
  (and (-> arg nil? not)
       (str/includes? arg ":")))

(defn unnamed? [arg]
  (and (-> arg nil? not)
       (-> arg named? not)))

(defn key-name [arg]
  (let [parts (str/split arg #":")
        n#parts (count parts)
        keyname (-> parts first keyword)]
    (cond
      (= "" (first parts)) [(keyword (str (second parts) "!")) "true"]
      (= 1 n#parts) [keyname ""]
      (= 2 n#parts) [keyname (second parts)]
      :else [keyname (vec (drop 1 parts))])))

(defn extract [args]
  (let [unnamed-args (filterv unnamed? args)
        named-args (into {} (map key-name
                                 (filterv named? args)))]
    {:named-args named-args
     :unnamed-args unnamed-args}))
