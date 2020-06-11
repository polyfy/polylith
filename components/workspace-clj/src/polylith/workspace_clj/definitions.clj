(ns polylith.workspace-clj.definitions
  (:require [clojure.string :as str]))

(def ->generic-type {'def "data"
                     'defn "function"
                     'defmacro "macro"})

(defn definition? [code]
  (if (list? code)
    (let [f (first code)]
      (or (= f 'def)
          (= f 'defn)
          (= f 'defmacro)))
    false))

(defn filter-statements [statements]
  (filterv definition?
           ; Drops the namespace declaration on top of the file
           (drop 1 statements)))

(defn sub-ns [namespace]
  (if (= "interface" namespace)
    ""
    (subs namespace 10)))

(defn ->function [namespace type name code]
  {:name (str name)
   :type (str type)
   :parameters (mapv str (first code))
   :sub-ns (sub-ns namespace)})

(defn ->definitions [namespace statement]
  "Takes a statement (def, defn or defmacro) from source code
   and returns a vector of generic definitions."
  (let [type (-> statement first ->generic-type)
        name (second statement)
        code (drop-while #(not (or (list? %)
                                   (vector? %)))
                         statement)]
    (if (= "data" type)
      [{:name (str name)
        :type (str type)
        :sub-ns (sub-ns namespace)}]
      (if (-> code first vector?)
        [(->function namespace type name code)]
        (mapv #(->function namespace type name %) code)))))
