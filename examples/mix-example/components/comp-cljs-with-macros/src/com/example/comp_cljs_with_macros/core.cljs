(ns com.example.comp-cljs-with-macros.core
  (:require [com.example.comp-cljs-with-macros.macros :refer [multiply]]))

(defn fn-9 [arg1 arg2 arg3]
  (multiply arg1 arg2 arg3))
