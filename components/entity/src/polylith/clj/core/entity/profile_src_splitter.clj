(ns polylith.clj.core.entity.profile-src-splitter
  (:require [clojure.string :as str]))

(defn test-path? [path]
  (str/ends-with? path "/test"))

(defn src-path? [path]
  (not (test-path? path)))

(defn extract-paths [dev? {:keys [profile->settings active-dev-profiles]}]
  (let [paths (mapcat :paths (map profile->settings active-dev-profiles))]
    {:profile-src-paths (if dev? (filterv src-path? paths) [])
     :profile-test-paths (if dev? (filterv test-path? paths) [])}))

(defn extract-deps [dev? {:keys [profile->settings active-dev-profiles]}]
  (if dev? (apply merge (map :lib-deps (map profile->settings active-dev-profiles)))
           {}))
