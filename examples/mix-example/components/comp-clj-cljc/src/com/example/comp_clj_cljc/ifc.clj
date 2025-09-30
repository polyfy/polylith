(ns com.example.comp-clj-cljc.ifc
  (:require [com.example.comp-clj-cljc.core :as core]
            [com.example.comp-clj-cljc.util :as util]))

(defn fn-1 [arg1 arg2]
  (core/fn-1 arg1 arg2))

(defn fn-2 [arg1]
  (util/fn-2 arg1))