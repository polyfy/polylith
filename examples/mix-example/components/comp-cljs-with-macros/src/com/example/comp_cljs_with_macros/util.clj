(ns com.example.comp-cljs-with-macros.util)

(defn add-debug-info
  [expr file line]
  ;; This runs at compile time to add debugging information
  `(try
     ~expr
     (catch :default e#
       (js/console.error 
         (str "Error in multiplication at " ~file ":" ~line)
         e#)
       0)))
