(ns polylith.clj.core.path-finder.interface.criterias
  (:require [polylith.clj.core.path-finder.criterias :as criterias]))

(defn =name [entity-name]
  (criterias/=name entity-name))

(defn component? [path-entry]
  (criterias/component? path-entry))

(defn base? [path-entry]
  (criterias/base? path-entry))

(defn brick? [path-entry]
  (criterias/brick? path-entry))

(defn project? [path-entry]
  (criterias/project? path-entry))

(defn exists? [path-entry]
  (criterias/exists? path-entry))

(defn filter-entries [path-entries criterias]
  (criterias/filter-entries path-entries criterias))

(defn has-entry? [path-entries criterias]
  (criterias/has-entry? path-entries criterias))

(defn match? [path-entry criterias]
  ((criterias/match? path-entry criterias)))

(defn not-exists? [path-entry]
  (criterias/not-exists? path-entry))

(defn not-test-or-resources-path [path-entry]
  (criterias/not-test-or-resources-path path-entry))

(defn profile? [path-entry]
  (criterias/profile? path-entry))

(defn not-profile? [path-entry]
  (criterias/not-profile? path-entry))

(defn resources-path? [path-entry]
  (criterias/resources-path? path-entry))

(defn src? [path-entry]
  (criterias/src? path-entry))

(defn src-path? [path-entry]
  (criterias/src-path? path-entry))

(defn test? [path-entry]
  (criterias/test? path-entry))

(defn test-path? [path-entry]
  (criterias/test-path? path-entry))
