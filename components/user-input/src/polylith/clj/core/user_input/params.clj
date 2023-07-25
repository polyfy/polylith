(ns ^:no-doc polylith.clj.core.user-input.params
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn named? [arg]
  (and (-> arg nil? not)
       (str/includes? arg ":")))

(defn unnamed? [arg]
  (and (-> arg nil? not)
       (-> arg named? not)))

(defn key-name [arg single-arg-commands]
  (let [parts (str-util/split-text arg ":")
        n#parts (count parts)
        keyname (-> parts first keyword)]
    (cond
      (= "" (first parts)) [(keyword (str (second parts) "!")) "true"]
      (= 1 n#parts) [keyname ""]
      (contains? single-arg-commands (first parts)) [keyname (str/join ":" (rest parts))]
      (= 2 n#parts) [keyname (second parts)]
      :else [keyname (vec (rest parts))])))

(defn extract [args single-arg-commands]
  (let [unnamed-args (filterv unnamed? args)
        named-args (into {} (map #(key-name % single-arg-commands)
                                 (filterv named? args)))]
    {:named-args named-args
     :unnamed-args unnamed-args}))
