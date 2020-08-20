(ns polylith.clj.core.workspace-clj.profile
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc :as util]))

(defn profile [[profile-key {:keys [extra-paths extra-deps]}]]
  [(subs (name profile-key) 1)
   (util/ordered-map :paths extra-paths
                     :lib-deps (util/stringify-and-sort-map extra-deps))])

(defn profile? [[alias]]
  (str/starts-with? (name alias) "+"))

(defn profile->settings [aliases]
  (into {} (map profile
                (filterv profile? aliases))))
