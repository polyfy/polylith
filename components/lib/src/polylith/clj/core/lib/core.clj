(ns polylith.clj.core.lib.core
  (:require [polylith.clj.core.lib.maven-dep :as maven-dep]
            [polylith.clj.core.lib.git-size :as git-size]
            [polylith.clj.core.lib.mvn-size :as mvn-size]
            [polylith.clj.core.lib.ns-to-lib :as ns-to-lib]
            [polylith.clj.core.lib.local-size :as local-size]
            [polylith.clj.core.util.interface :as util]))

(defn with-size [[name {:keys [mvn/version local/root git/url sha] :as value}] user-home]
  (cond
    version (mvn-size/with-size name version value)
    root (local-size/with-size-and-version name root value)
    url (git-size/with-size-and-version name sha value user-home)))

(defn latest-lib-version [result [k v2]]
  (if (:mvn/version v2)
    (let [v1 (result k v2)
          v (maven-dep/latest v1 v2)]
      (assoc result k v))
    (assoc result k v2)))

(defn with-sizes [libraries user-home]
  (util/stringify-and-sort-map
    (into {} (mapv #(with-size % user-home)
                   (reduce latest-lib-version {} libraries)))))

(defn lib-deps [ws-type config top-namespace ns-to-lib namespaces user-home dep-keys]
  (if (= :toolsdeps1 ws-type)
    (ns-to-lib/lib-deps top-namespace ns-to-lib namespaces)
    (with-sizes (get-in config dep-keys) user-home)))

(defn brick-lib-deps [ws-type config top-namespace ns-to-lib namespaces user-home]
  (let [src (lib-deps ws-type config top-namespace ns-to-lib (:src namespaces) user-home [:deps])
        test (lib-deps ws-type config top-namespace ns-to-lib (:test namespaces) user-home [:aliases :test :extra-deps])]
    (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))
