(ns com.example.comp-clj-cljc-cljs.core)

(defn fn-8 [[arg1 arg2 arg3]]
  (js/Math.round (* arg3 (js/Math.pow arg1 arg2))))
