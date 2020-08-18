(ns polylith.clj.core.path-finder.profile-src-splitter
  (:require [clojure.string :as str]))

(defn test-path? [path]
  (str/ends-with? path "/test"))

(defn src-path? [path]
  (not (test-path? path)))

(defn src-paths [dev? paths]
  (if dev? (filterv src-path? paths) []))

(defn test-paths [dev? paths]
  (if dev? (filterv test-path? paths) []))

(defn extract-active-dev-profiles-paths [dev? {:keys [profile->settings active-dev-profiles]}]
  (let [paths (mapcat :paths (map #(profile->settings %) active-dev-profiles))]
    {:profile-src-paths (src-paths dev? paths)
     :profile-test-paths (test-paths dev? paths)}))

(defn extract-profile-paths [profile-name {:keys [profile->settings]}]
  (let [paths (-> profile-name profile->settings :paths)]
    {:src-paths (src-paths true paths)
     :test-paths (test-paths true paths)}))
