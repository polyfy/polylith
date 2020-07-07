(ns polylith.clj.core.validate.shared
  (:require [clojure.string :as str]))

(defn full-name
  ([{:keys [sub-ns name]}]
   (full-name sub-ns name))
  ([sub-ns name]
   (if (str/blank? sub-ns)
     name
     (str sub-ns "/" name))))

(defn with-ns [sub-ns name]
  (if (str/blank? sub-ns)
    name
    (str sub-ns "." name)))

(defn parameter [{:keys [name type]}]
  (if type
    (str type " " name)
    name))

(defn ->function-or-macro
  ([{:keys [sub-ns name parameters]}]
   (->function-or-macro sub-ns name parameters))
  ([sub-ns name parameters]
   (str (with-ns sub-ns name) "[" (str/join " " (map parameter parameters)) "]")))

(defn function->id [{:keys [name parameters]}]
  [name (count parameters)])

(defn id->functions-or-macro [{:keys [definitions]}]
  (group-by function->id
            (filter #(not= "data" (:type %)) definitions)))
