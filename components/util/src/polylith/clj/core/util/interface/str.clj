(ns ^:no-doc polylith.clj.core.util.interface.str
  (:require [clojure.string :as str]
            [polylith.clj.core.util.str :as str-util])
  (:refer-clojure :exclude [drop-last]))

(defn drop-last [n string]
  (str-util/drop-last n string))

(defn skip-if-ends-with [string ends-with]
  (when string
    (if (str/ends-with? string ends-with)
      (let [chars (- (count string) (count ends-with))]
        (subs string 0 chars))
      string)))

(defn skip-until [string separator]
  (str-util/skip-until string separator))

(defn take-until [string separator]
  (str-util/take-until string separator))

(defn skip-prefix [string prefix]
  (str-util/skip-prefix string prefix))

(defn skip-suffix [string suffix]
  (str-util/skip-suffix string suffix))

(defn skip-suffixes [string suffixes]
  (str-util/skip-suffixes string suffixes))

(defn spaces [n#spaces]
  (str-util/spaces n#spaces))

(defn split-text
  ([text]
   (str-util/split-text text " "))
  ([text separator]
   (str-util/split-text text separator)))

(defn line [length]
  (str-util/line length))

(defn count-things [thing cnt]
  (if (<= cnt 1)
    (str cnt " " thing)
    (str cnt " " thing "s")))

(defn sep-1000 [number sep]
  (if (integer? number)
    (let [num (format "%,d" number (java.util.Locale/UK))]
      (str/replace num " " (or sep ",")))
    number))
