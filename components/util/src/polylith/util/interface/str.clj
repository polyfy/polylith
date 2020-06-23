(ns polylith.util.interface.str
  (:require [polylith.util.str :as str]))

(defn skip-until [string separator]
  (str/skip-until string separator))

(defn skip-suffix [string suffix]
  (str/skip-suffix string suffix))

(defn skip-suffixes [string suffixes]
  (str/skip-suffixes string suffixes))
