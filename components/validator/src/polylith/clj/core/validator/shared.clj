(ns ^:no-doc polylith.clj.core.validator.shared
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

(defn function->id [{:keys [name sub-ns parameters]}]
  [name sub-ns (count parameters)])

(defn id->functions-or-macro [{:keys [definitions]}]
  (group-by function->id
            (filter #(not= "data" (:type %)) definitions)))

(defn- libs [{:keys [lib-deps]}]
  (map first (concat (:src lib-deps)
                     (:test lib-deps))))

(defn profile-lib [[_ {:keys [lib-deps]}]]
  (map first lib-deps))

(defn used-libs [projects settings]
  (set (concat (mapcat libs projects)
               (mapcat profile-lib
                       (:profile-to-settings settings)))))

(defn show-error? [cmd profile-to-settings active-profiles]
  ; When we have at least one profile and the user has deselected all active
  ; profiles by passing in "+" as an argument, then don't show the error
  ; when running the 'info' command.
  (or (not= "info" cmd)
      (-> profile-to-settings empty?)
      (-> active-profiles empty? not)))
