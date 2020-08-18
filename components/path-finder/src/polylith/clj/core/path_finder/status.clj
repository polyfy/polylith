(ns polylith.clj.core.path-finder.status
  (:require [polylith.clj.core.path-finder.matchers :as m]))

(defn status-flag [path-entries name & criterias]
  (let [statuses (concat [m/exists? (m/=name name)] criterias)
        standard? (m/has-entry? path-entries (conj statuses m/standard?))
        profile? (m/has-entry? path-entries (conj statuses m/profile?))]
    (if standard?
      "x"
      (if profile? "+" "-"))))

(defn src-status-flag [path-entries category-criteria name]
  (status-flag path-entries name m/src? m/src-path? category-criteria))

(defn resources-status-flag [path-entries category-criteria name show-resources?]
  (if show-resources?
    (status-flag path-entries name m/src? m/resources-path? category-criteria)
    ""))

(defn test-status-flag [path-entries category-criteria name]
  (status-flag path-entries name m/test? m/test-path? category-criteria))

(def category->criteria {:brick m/brick?
                         :env   m/environment?})

(defn status-flags [path-entries category name show-resources?]
  (let [category-criteria (category->criteria category)]
    (str (src-status-flag path-entries category-criteria name)
         (resources-status-flag path-entries category-criteria name show-resources?)
         (test-status-flag path-entries category-criteria name))))
