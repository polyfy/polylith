(ns polylith.clj.core.workspace-clj.profile
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace-clj.library :as library]))

(defn profile [[profile-key {:keys [extra-paths extra-deps]}]]
  [(subs (name profile-key) 1)
   (util/ordered-map :paths extra-paths
                     :lib-deps (library/with-sizes extra-deps))])

(defn profile? [[alias]]
  (str/starts-with? (name alias) "+"))

(defn profile-to-settings [aliases]
  (into {} (map profile
                (filterv profile? aliases))))
