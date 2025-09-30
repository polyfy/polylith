(ns com.example.comp-cljs-with-macros.macros
  (:require [com.example.comp-cljs-with-macros.util :as util]))

(defmacro multiply
  [a b c]
  (util/add-debug-info
    `(* ~a ~b ~c)
    *file*
    (:line (meta &form))))
