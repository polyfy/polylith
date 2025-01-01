(ns com.example.comp-cljc.ifc
  (:require [com.example.comp-clj-cljc-cljs.ifc :as comp-clj-cljc-cljs]))

(defn fn-4 [arg1 arg2 arg3]
  (comp-clj-cljc-cljs/fn-8 {:key-1 arg1 :key-2 arg2 :key-3 arg3}))

(defn fn-5 [opts]
  (comp-clj-cljc-cljs/fn-8 opts))
