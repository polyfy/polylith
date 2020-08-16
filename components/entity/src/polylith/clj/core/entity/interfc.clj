(ns polylith.clj.core.entity.interfc
  (:require [polylith.clj.core.entity.core :as core])
  (:require [polylith.clj.core.entity.dep-selector :as dep-selector])
  (:require [polylith.clj.core.entity.path-selector :as path-selector]))

(defn path-entries [ws-dir dev? src-paths test-paths settings]
  (core/path-entries ws-dir dev? src-paths test-paths settings))

(defn deps-entries [dev? src-deps test-deps settings]
  (core/deps-entries dev? src-deps test-deps settings))

(defn all-src-deps [dep-entries]
  (dep-selector/all-src-deps dep-entries))

(defn all-test-deps [dep-entries]
  (dep-selector/all-test-deps dep-entries))

(defn src-paths [path-entries]
  (path-selector/all-src-paths path-entries))

(defn test-paths [path-entries]
  (path-selector/all-test-paths path-entries))

(defn src-component-names [path-entries]
  (path-selector/src-component-names path-entries))

(defn src-base-names [path-entries]
  (path-selector/src-base-names path-entries))

(defn src-brick-names [path-entries]
  (path-selector/src-brick-names path-entries))

(defn test-component-names [path-entries]
  (path-selector/test-component-names path-entries))

(defn test-base-names [path-entries]
  (path-selector/test-base-names path-entries))
