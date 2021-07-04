(ns polylith.clj.core.lib.core
  (:require [polylith.clj.core.lib.maven-dep :as maven-dep]
            [polylith.clj.core.lib.size :as size]
            [polylith.clj.core.lib.ns-to-lib :as ns-to-lib]
            [polylith.clj.core.util.interface :as util]))

(defn latest-lib-version [result [k v2]]
  (if (:mvn/version v2)
    (let [v1 (result k v2)
          v (maven-dep/latest v1 v2)]
      (assoc result k v))
    (assoc result k v2)))

(defn latest-with-sizes [libraries user-home]
  (util/stringify-and-sort-map
    (into {} (size/with-sizes (reduce latest-lib-version {} libraries)
                              user-home))))

(defn lib-deps [ws-type config top-namespace ns-to-lib namespaces user-home dep-keys]
  (if (= :toolsdeps1 ws-type)
    (ns-to-lib/lib-deps top-namespace ns-to-lib namespaces)
    (latest-with-sizes (get-in config dep-keys) user-home)))

(defn brick-lib-deps [ws-type config top-namespace ns-to-lib namespaces user-home]
  (let [src (lib-deps ws-type config top-namespace ns-to-lib (:src namespaces) user-home [:deps])
        test (lib-deps ws-type config top-namespace ns-to-lib (:test namespaces) user-home [:aliases :test :extra-deps])]
    (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))
