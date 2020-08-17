(ns polylith.clj.core.workspace-clj.profile
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc :as util]))

(defn profile [[profile-key {:keys [extra-paths extra-deps]}]]
  [(subs (name profile-key) 4)
   (util/ordered-map :paths extra-paths
                     :lib-deps (util/stringify-and-sort-map extra-deps))])

(defn profile->settings [aliases]
  (into {} (map profile
                (filterv #(str/starts-with? (-> % first name) "dev+") aliases))))
