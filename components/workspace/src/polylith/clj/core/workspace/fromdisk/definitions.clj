(ns ^:no-doc polylith.clj.core.workspace.fromdisk.definitions
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface :as util]))

(def ->generic-type {'def "data"
                     'defn "function"
                     'defmacro "macro"})

(defn definition? [code]
  (if (list? code)
    (let [f (first code)
          private? (-> code second meta :private)]
      (and (not private?)
           (or (= f 'def)
               (= f 'defn)
               (= f 'defmacro))))
    false))

(defn filter-statements [statements]
  (filterv definition?
           ; Drops the namespace declaration on top of the file
           (drop 1 statements)))

(defn sub-namespace [namespace interface-ns]
  (when (not (common/top-interface-ns? namespace interface-ns))
    (str/join "." (drop 1 (str/split namespace #"\.")))))

(defn argument [name]
  (let [type (-> name meta :tag)]
    (if type
      {:name (str name)
       :type (str "^" type)}
      {:name (str name)})))

(defn function [namespace type name code interface-ns]
  (let [sub-ns (sub-namespace namespace interface-ns)
        arglist (mapv argument (first code))
        str-name (str name)
        str-type (str type)]
    (util/ordered-map :name str-name
                      :type str-type
                      :arglist arglist
                      :sub-ns sub-ns)))

(defn definitions
  "Takes a statement (def, defn or defmacro) from source code
   and returns a vector of definitions."
  [namespace statement interface-ns]
  (let [type (-> statement first ->generic-type)
        name (second statement)
        code (drop-while #(not (or (list? %)
                                   (vector? %)))
                         statement)]
    (if (= "data" type)
      [(util/ordered-map :name (str name)
                         :type (str type)
                         :sub-ns (sub-namespace namespace interface-ns))]
      (if (-> code first vector?)
        [(function namespace type name code interface-ns)]
        (mapv #(function namespace type name % interface-ns) code)))))
