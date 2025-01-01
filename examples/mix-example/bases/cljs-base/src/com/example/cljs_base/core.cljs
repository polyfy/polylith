(ns com.example.cljs-base.core
  (:require ["react" :as react]
            ["react-dom" :as react.dom]
            [com.example.comp-cljs-cljc.ifc :as comp-cljs-cljc]
            [com.example.comp-cljs-only.ifc :as comp-cljs-only]))

(defn init []
  (println "Hello World!"))
