(ns polylith.util.str
  (:require [clojure.string :as str]
            [polylith.util.core :as core]))

(defn skip-until [string separator]
  (when string
    (let [index (str/index-of string separator)]
      (if (< index 0)
        string
        (subs string (inc index))))))

(defn skip-suffix [string suffix]
  (when string
    (if (str/ends-with? string suffix)
      (let [string-cnt (count string)
            suffix-cnt (count suffix)]
        (if (< suffix-cnt string-cnt)
          (subs string 0 (- string-cnt suffix-cnt))
          string))
      string)))

(defn skip-suffixes [string suffixes]
  (if-let [suffix (core/find-first #(str/ends-with? string %) suffixes)]
    (skip-suffix string suffix)
    string))

(defn spaces [length]
  (str/join (take length (repeat " "))))

(defn line [length]
  (str/join (take length (repeat "-"))))
