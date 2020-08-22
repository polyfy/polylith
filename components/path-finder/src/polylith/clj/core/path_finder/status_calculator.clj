(ns polylith.clj.core.path-finder.status-calculator
  (:require [polylith.clj.core.path-finder.interfc.criterias :as c]))

(defn status-flag [path-entries name & criterias]
  (let [statuses (concat [c/exists? (c/=name name)] criterias)]
    (if (c/has-entry? path-entries statuses)
      "x"
      "-")))

(defn src-status-flag [path-entries category-criteria name]
  (status-flag path-entries name c/src? c/src-path? category-criteria))

(defn resources-status-flag [path-entries category-criteria name show-resources?]
  (if show-resources?
    (status-flag path-entries name c/src? c/resources-path? category-criteria)
    ""))

(defn test-status-flag [path-entries category-criteria name]
  (status-flag path-entries name c/test? c/test-path? category-criteria))

(defn status-flags [path-entries category-criteria name show-resources?]
  (str (src-status-flag path-entries category-criteria name)
       (resources-status-flag path-entries category-criteria name show-resources?)
       (test-status-flag path-entries category-criteria name)))
