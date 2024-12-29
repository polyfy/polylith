(ns com.example.comp-cljs-cljc.ifc
  (:require [com.example.comp-cljs-cljc.core :as core]
            [com.example.comp-cljs-cljc.util :as util]))

(defn fn-1 [arg1 arg2]
  (core/fn-1 arg1 arg2))

(defn fn-3 [arg1]
  (util/fn-3 arg1))
