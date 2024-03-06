(ns poly-rcf.sqldb.interface
  (:require [poly-rcf.sqldb.core :as core]))


(defn new-component
  "Returns new sqldb system component."
  [cfg tag]
  (core/new-component cfg tag))