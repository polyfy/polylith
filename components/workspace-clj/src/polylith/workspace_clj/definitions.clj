(ns polylith.workspace-clj.definitions)

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

(defn sub-namespace [namespace]
  (when (not (= "interface" namespace))
    (subs namespace 10)))

(defn function [namespace type name code]
  (let [sub-ns (sub-namespace namespace)
        parameters (mapv str (first code))
        str-name (str name)
        str-type (str type)]
    (if sub-ns
      {:name str-name
       :type str-type
       :parameters parameters
       :sub-ns sub-ns}
      {:name str-name
       :type str-type
       :parameters parameters})))

(defn definitions [namespace statement]
  "Takes a statement (def, defn or defmacro) from source code
   and returns a vector of definitions."
  (let [type (-> statement first ->generic-type)
        name (second statement)
        code (drop-while #(not (or (list? %)
                                   (vector? %)))
                         statement)]
    (if (= "data" type)
      [{:name (str name)
        :type (str type)
        :sub-ns (sub-namespace namespace)}]
      (if (-> code first vector?)
        [(function namespace type name code)]
        (mapv #(function namespace type name %) code)))))
