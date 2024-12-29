(ns com.example.comp-clj-cljc-cljs.core 
  (:require [clojure.math :as math]))

(defn fn-8 [[arg1 arg2 arg3]]
  (math/round (* arg3 (math/pow arg1 arg2))))
