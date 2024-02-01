(ns ^:no-doc polylith.clj.core.validator.shared
  (:require [clojure.string :as str]))

(defn error-message? [{:keys [type]}]
  (= "error" type))

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

(defn argument [{:keys [name type]}]
  (if type
    (str type " " name)
    name))

(defn ->function-or-macro
  ([{:keys [sub-ns name arglist]}]
   (->function-or-macro sub-ns name arglist))
  ([sub-ns name arglist]
   (str (with-ns sub-ns name) "[" (str/join " " (map argument arglist)) "]")))

(defn function->id [{:keys [name sub-ns arglist]}]
  [name sub-ns (count arglist)])

(defn id->functions-or-macro [{:keys [definitions]}]
  (group-by function->id
            (filter #(not= "data" (:type %)) definitions)))

(defn show-error? [cmd profiles active-profiles]
  ; When we have at least one profile and the user has deselected all active
  ; profiles by passing in "+" as an argument, then don't show the error
  ; when running the 'info' command.
  (or (not= "info" cmd)
      (-> profiles empty?)
      (-> active-profiles empty? not)))
