(ns polylith.clj.core.lib.core
  (:require [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.lib.deps :as deps]
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

(defn lib-deps [ws-type config top-namespace ns-to-lib namespaces user-home dep-keys]
  (if (= :toolsdeps2 ws-type)
    (with-sizes (get-in config dep-keys) user-home)
    (deps/lib-deps top-namespace ns-to-lib namespaces)))

(defn lib-deps-src [ws-type config top-namespace ns-to-lib namespaces-src user-home]
  (lib-deps ws-type config top-namespace ns-to-lib namespaces-src user-home [:deps]))

(defn lib-deps-test [ws-type config top-namespace ns-to-lib namespaces-test user-home]
  (lib-deps ws-type config top-namespace ns-to-lib namespaces-test user-home [:aliases :test :extra-deps]))
