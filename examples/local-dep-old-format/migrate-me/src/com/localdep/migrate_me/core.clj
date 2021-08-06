(ns com.localdep.migrate-me.core
  (:require [clj-time.core :as clj-time]))

(def one-two-three 123)

(defn hour-22 []
  (clj-time/hour (clj-time/date-time 1986 10 14 22)))
