(ns polylith.clj.core.workspace.profile
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc :as util]))

(defn src-path? [path]
  (and (or (str/starts-with? path "components/")
           (str/starts-with? path "bases/"))
       (not (str/ends-with? path "/test"))))

(defn test-path? [path]
  (and (or (str/starts-with? path "components/")
           (str/starts-with? path "bases/"))
       (str/ends-with? path "/test")))

(defn- with-deps [deps profile-deps]
  (merge deps (util/stringify-and-sort-map profile-deps)))

(defn lib-deps [env deps active-dev-profiles profile->settings]
  (if (= "development" env)
    (reduce #(with-deps %1 (-> %2 profile->settings :deps))
            deps
            active-dev-profiles)
    deps))

(defn- with-paths [path? paths profile-paths]
  (concat paths (filter path? profile-paths)))

(defn select-src-paths [path? env paths active-dev-profiles profile->settings]
  (if (= "development" env)
    (vec (sort (reduce #(with-paths path? %1 (-> %2 profile->settings :paths))
                       paths
                       active-dev-profiles)))
    paths))

(defn src-paths [env paths active-dev-profiles profile->settings]
  (select-src-paths src-path? env paths active-dev-profiles profile->settings))

(defn test-paths [env paths active-dev-profiles profile->settings]
  (select-src-paths test-path? env paths active-dev-profiles profile->settings))
