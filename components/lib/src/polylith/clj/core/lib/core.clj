(ns ^:no-doc polylith.clj.core.lib.core
  (:require [polylith.clj.core.config-reader.interface :as config-reader]
            [polylith.clj.core.lib.size :as size]
            [polylith.clj.core.lib.ns-to-lib :as ns-to-lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.maven.interface :as maven]))

(defn latest-lib-version [result [k v2]]
  (if (:mvn/version v2)
    (let [v1 (result k v2)
          v (maven/latest-lib v1 v2 :mvn/version)]
      (assoc result k v))
    (assoc result k v2)))

(defn latest-with-sizes [ws-dir entity-root-path libraries user-home]
  (util/stringify-and-sort-map
    (into {} (size/with-sizes-vec ws-dir
                                  entity-root-path
                                  (reduce latest-lib-version {} libraries)
                                  user-home))))

(defn lib-deps [ws-dir ws-type config top-namespace ns-to-lib lib->deps namespaces entity-root-path user-home dep-keys]
  (if (= :toolsdeps1 ws-type)
    (ns-to-lib/lib-deps ws-dir top-namespace ns-to-lib lib->deps namespaces user-home)
    (latest-with-sizes ws-dir entity-root-path (get-in config dep-keys) user-home)))

(defn lib->deps [ws-dir ws-type]
  (let [{:keys [deps]} (config-reader/read-development-deps-file ws-dir ws-type)]
    (util/stringify-and-sort-map (merge (-> deps :aliases :test :extra-deps)
                                        (-> deps :aliases :dev :extra-deps)))))

(defn brick-lib-deps [ws-dir ws-type config top-namespace ns-to-lib namespaces entity-root-path user-home]
  (let [lib->deps (lib->deps ws-dir ws-type)
        src (lib-deps ws-dir ws-type config top-namespace ns-to-lib lib->deps (:src namespaces) entity-root-path user-home [:deps])
        test (lib-deps ws-dir ws-type config top-namespace ns-to-lib lib->deps (:test namespaces) entity-root-path user-home [:aliases :test :extra-deps])]
    (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))
