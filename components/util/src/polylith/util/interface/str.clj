(ns polylith.util.interface.str
  (:require [clojure.string :as str]
            [polylith.util.interface :as util]))

(defn skip-until [string separator]
  (let [index (str/index-of string separator)]
    (if (< index 0)
      string
      (subs string (inc index)))))

(defn skip-suffix [string suffix]
  (if (str/ends-with? string suffix)
    (let [string-cnt (count string)
          prefix-cnt (count suffix)]
      (if (< prefix-cnt string-cnt)
        (subs string 0 (- string-cnt prefix-cnt))
        string))
    string))

(defn skip-suffixes [string prefixes]
  (if-let [suffix (util/find-first #(str/ends-with? string %) prefixes)]
    (skip-suffix string suffix)
    string))
