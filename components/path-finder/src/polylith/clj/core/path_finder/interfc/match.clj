(ns polylith.clj.core.path-finder.interfc.match
  (:require [polylith.clj.core.path-finder.matcher :as match]))

(defn =name [entity-name]
  (match/=name entity-name))

(defn component? [path-entry]
  (match/component? path-entry))

(defn base? [path-entry]
  (match/base? path-entry))

(defn brick? [path-entry]
  (match/brick? path-entry))

(defn environment? [path-entry]
  (match/environment? path-entry))

(defn exists? [path-entry]
  (match/exists? path-entry))

(defn filter-entries [path-entries criterias]
  (match/filter-entries path-entries criterias))

(defn has-entry? [path-entries criterias]
  (match/has-entry? path-entries criterias))

(defn match? [path-entry criterias]
  ((match/match? path-entry criterias)))

(defn not-exists? [path-entry]
  (match/not-exists? path-entry))

(defn not-test-or-resources-path [path-entry]
  (match/not-test-or-resources-path path-entry))

(defn profile? [path-entry]
  (match/profile? path-entry))

(defn resources-path? [path-entry]
  (match/resources-path? path-entry))

(defn src? [path-entry]
  (match/src? path-entry))

(defn src-path? [path-entry]
  (match/src-path? path-entry))

(defn test? [path-entry]
  (match/test? path-entry))

(defn test-path? [path-entry]
  (match/test-path? path-entry))

(defn standard? [path-entry]
  (match/standard? path-entry))
