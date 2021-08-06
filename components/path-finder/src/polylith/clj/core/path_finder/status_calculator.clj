(ns polylith.clj.core.path-finder.status-calculator
  (:require [polylith.clj.core.path-finder.interface.criterias :as c]))

(defn status-flag [path-entries name flag & criterias]
  (let [statuses (concat [c/exists? (c/=name name)] criterias)]
    (if (c/has-entry? path-entries statuses)
      flag
      "-")))

(defn src-status-flag [path-entries name entity-criteria]
  (status-flag path-entries name "s" c/src? c/src-path? entity-criteria))

(defn resources-status-flag [path-entries name is-show-resources entity-criteria]
  (if is-show-resources
    (status-flag path-entries name "r" c/src? c/resources-path? entity-criteria)
    ""))

(defn test-status-flag [path-entries name entity-criteria path-criteria]
  (status-flag path-entries name "t" c/test? entity-criteria path-criteria))

(defn status-flags [path-entries name is-show-resources entity-criteria path-criteria]
  (str (src-status-flag path-entries name entity-criteria)
       (resources-status-flag path-entries name is-show-resources entity-criteria)
       (test-status-flag path-entries name entity-criteria path-criteria)))
