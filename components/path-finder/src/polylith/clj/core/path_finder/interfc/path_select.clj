(ns polylith.clj.core.path-finder.interfc.path-select
  (:require [polylith.clj.core.path-finder.dep-selector :as dep-selector]
            [polylith.clj.core.path-finder.path-selector :as path-selector]))

(defn profile-src-paths [path-entries]
  (path-selector/profile-src-paths path-entries))

(defn profile-test-paths [path-entries]
  (path-selector/profile-test-paths path-entries))

(defn missing-paths-except-test-and-resources [path-entries]
  (path-selector/missing-paths-except-test-and-resources path-entries))

(defn all-src-paths [path-entries]
  (path-selector/all-src-paths path-entries))

(defn all-test-paths [path-entries]
  (path-selector/all-test-paths path-entries))

(defn src-component-names [path-entries]
  (path-selector/src-component-names path-entries))

(defn src-base-names [path-entries]
  (path-selector/src-base-names path-entries))

(defn src-environment-names [path-entries]
  (path-selector/src-environment-names path-entries))

(defn src-brick-names [path-entries]
  (path-selector/src-brick-names path-entries))

(defn test-component-names [path-entries]
  (path-selector/test-component-names path-entries))

(defn test-base-names [path-entries]
  (path-selector/test-base-names path-entries))
