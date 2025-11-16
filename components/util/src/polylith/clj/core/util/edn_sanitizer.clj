(ns ^:no-doc polylith.clj.core.util.edn-sanitizer
  (:require [clojure.string :as str]
            [clojure.walk :as walk]))

(defn illegal-keyword? [k]
  (and (keyword? k)
       (or (when-let [ns (namespace k)]
             (str/includes? ns "@"))
           (str/includes? (name k) "@"))))

(defn keyword->string [k]
  (if-let [ns (namespace k)]
    (str ns "/" (name k))
    (name k)))

(defn sanitize-keywords [edn]
  (walk/postwalk
    (fn [form]
      (cond
        (map? form) (reduce-kv (fn [m k v]
                                 (if (illegal-keyword? k)
                                   (assoc m (keyword->string k) v)
                                   (assoc m k v)))
                               {}
                               form)
        (illegal-keyword? form) (keyword->string form)
        :else form))
    edn))
