(ns polylith.clj.core.util.interfc.str
  (:require [polylith.clj.core.util.str :as str]))

(defn skip-until [string separator]
  (str/skip-until string separator))

(defn skip-prefix [string prefix]
  (str/skip-prefix string prefix))

(defn skip-suffix [string suffix]
  (str/skip-suffix string suffix))

(defn skip-suffixes [string suffixes]
  (str/skip-suffixes string suffixes))

(defn spaces [length]
  (str/spaces length))

(defn line [length]
  (str/line length))

(defn count-things [thing cnt]
  (if (<= cnt 1)
    (str cnt " " thing)
    (str cnt " " thing "s")))
