(ns polylith.clj.core.lib.core
  (:require [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.lib.git-size :as git-size]
            [polylith.clj.core.lib.mvn-size :as mvn-size]
            [polylith.clj.core.lib.local-size :as local-size]))

(defn with-size [[name {:keys [mvn/version local/root git/url sha] :as value}] user-home]
  (cond
    version (mvn-size/with-size name version value)
    root (local-size/with-size-and-version name root value)
    url (git-size/with-size-and-version name sha value user-home)
    :else nil))

(defn with-sizes [library-map user-home]
  (util/stringify-and-sort-map (into {} (map #(with-size % user-home) library-map))))
