(ns polylith.clj.core.path-finder.profile-src-splitter
  (:require [clojure.string :as str]))

(defn test-path? [path]
  (str/ends-with? path "/test"))

(defn src-path? [path]
  (not (test-path? path)))

(defn src-paths [is-dev paths]
  (if is-dev (filterv src-path? paths)
             []))

(defn test-paths [is-dev paths]
  (if is-dev (filterv test-path? paths)
             []))

(defn extract-active-profiles-paths [is-dev {:keys [profile-to-settings active-profiles]}]
  (let [paths (mapcat :paths (map #(profile-to-settings %) active-profiles))]
    {:profile-src-paths (src-paths is-dev paths)
     :profile-test-paths (test-paths is-dev paths)}))

(defn extract-profile-paths [profile-name {:keys [profile-to-settings]}]
  (let [paths (-> profile-name profile-to-settings :paths)]
    {:src-paths (src-paths true paths)
     :test-paths (test-paths true paths)}))
