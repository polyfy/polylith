(ns com.example.comp-cljs-only.ifc
  (:require [com.example.comp-cljc.ifc :as comp-cljc]
            [com.example.comp-cljs-with-macros.ifc :as comp-cljs-with-macros]))

(defn fn-7 [arg1 arg2 arg3]
  (comp-cljc/fn-5 {:key-1 arg3 :key-2 arg2 :key-3 arg1}))

(defn fn-10 []
  (comp-cljs-with-macros/fn-9 [3 4 5]))
