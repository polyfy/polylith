(ns com.example.comp-clj-only.ifc
  (:require [com.example.comp-cljc.ifc :as comp-cljc]))

(defn fn-6 [arg1 arg2 arg3]
  (comp-cljc/fn-4 arg3 arg2 arg1))
