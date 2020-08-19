(ns polylith.clj.core.path-finder.interfc
  (:require [polylith.clj.core.path-finder.status :as status]
            [polylith.clj.core.path-finder.core :as core]
            [polylith.clj.core.path-finder.dep-selector :as dep-selector]
            [polylith.clj.core.path-finder.path-selector :as path-selector]
            [polylith.clj.core.path-finder.dep-extractor :as dep-extractor]))

(defn path-entries-including-settings [ws-dir dev? src-paths test-paths settings]
  (core/path-entries-from-settings ws-dir dev? src-paths test-paths settings))

(defn path-entries [ws-dir src-paths test-paths profile-src-paths profile-test-paths]
  (core/path-entries ws-dir src-paths test-paths profile-src-paths profile-test-paths))

(defn profile-entries [ws-dir settings profile-name]
  (core/profile-path-entries ws-dir settings profile-name))

(defn deps-entries [dev? src-deps test-deps settings]
  (dep-extractor/dep-entries dev? src-deps test-deps settings))

(defn all-src-deps [dep-entries]
  (dep-selector/all-src-deps dep-entries))

(defn all-test-deps [dep-entries]
  (dep-selector/all-test-deps dep-entries))

(defn profile-src-paths [path-entries]
  (path-selector/profile-src-paths path-entries))

(defn profile-test-paths [path-entries]
  (path-selector/profile-test-paths path-entries))

(defn all-src-paths [path-entries]
  (path-selector/all-src-paths path-entries))

(defn all-test-paths [path-entries]
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

(defn brick-status-flags [path-entries brick-name show-resources?]
  (status/status-flags path-entries :brick brick-name show-resources?))

(defn env-status-flags [path-entries env-name show-resources?]
  (status/status-flags path-entries :env env-name show-resources?))
