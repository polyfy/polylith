(ns polylith.clj.core.path-finder.status-calculator
  (:require [polylith.clj.core.path-finder.interfc.match :as m]))

(defn status-flag [path-entries name & criterias]
  (let [statuses (concat [m/exists? (m/=name name)] criterias)]
    (if (m/has-entry? path-entries statuses)
      "x"
      "-")))

(defn src-status-flag [path-entries category-criteria name]
  (status-flag path-entries name m/src? m/src-path? category-criteria))

(defn resources-status-flag [path-entries category-criteria name show-resources?]
  (if show-resources?
    (status-flag path-entries name m/src? m/resources-path? category-criteria)
    ""))

(defn test-status-flag [path-entries category-criteria name]
  (status-flag path-entries name m/test? m/test-path? category-criteria))

(defn status-flags [path-entries category-criteria name show-resources?]
  (str (src-status-flag path-entries category-criteria name)
       (resources-status-flag path-entries category-criteria name show-resources?)
       (test-status-flag path-entries category-criteria name)))
