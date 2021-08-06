(ns com.localdep.util.interface
  (:require [clj-time.core :as clj-time]))

(defn do-stuff [string1 string2]
  (str string1 string2))

(defn date-midnight-2021 []
  (clj-time/date-midnight 2021))
