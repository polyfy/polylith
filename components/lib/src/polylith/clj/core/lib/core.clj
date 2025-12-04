(ns ^:no-doc polylith.clj.core.lib.core
  (:require [polylith.clj.core.config-reader.interface :as config-reader]
            [polylith.clj.core.lib.size-deps :as size-deps]
            [polylith.clj.core.lib.size-npm :as size-npm]
            [polylith.clj.core.lib.ns-to-lib :as ns-to-lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.maven.interface :as maven]))

(defn- latest-lib-version [result [k v2]]
  (if (:mvn/version v2)
    (let [v1 (result k v2)
          v (maven/latest-lib v1 v2 :mvn/version)]
      (assoc result k v))
    (assoc result k v2)))

(defn- latest-tools-deps-with-sizes-vec [ws-dir entity-root-path libraries user-home]
  (size-deps/with-sizes-vec ws-dir
                            entity-root-path
                            (reduce latest-lib-version {} libraries)
                            user-home))

(defn latest-tools-deps-with-sizes [ws-dir entity-root-path libraries user-home]
  (into {}
        (latest-tools-deps-with-sizes-vec ws-dir entity-root-path libraries user-home)))


(defn lib-deps [ws-dir ws-type deps package-deps package-name top-namespace ns-to-lib lib->deps namespaces entity-root-path user-home]
  (if (= :toolsdeps1 ws-type)
    (ns-to-lib/lib-deps ws-dir top-namespace ns-to-lib lib->deps namespaces user-home)
    (util/stringify-and-sort-map
      (into {} (concat (latest-tools-deps-with-sizes-vec ws-dir entity-root-path deps user-home)
                       (size-npm/with-sizes-vec ws-dir package-name package-deps))))))

(defn lib->deps [ws-dir ws-type]
  (let [{:keys [deps]} (config-reader/read-development-config-files ws-dir ws-type)]
    (util/stringify-and-sort-map (merge (-> deps :aliases :test :extra-deps)
                                        (-> deps :aliases :dev :extra-deps)))))

(defn brick-lib-deps [ws-dir ws-type deps-config package-config top-namespace ns-to-lib namespaces entity-root-path user-home]
  (let [lib->deps (lib->deps ws-dir ws-type)
        src-deps (:deps deps-config)
        test-deps (get-in deps-config [:aliases :test :extra-deps])
        package-name (:name package-config)
        package-src-deps (:dependencies package-config)
        package-test-deps (:devDependencies package-config)
        src (lib-deps ws-dir ws-type src-deps package-src-deps package-name top-namespace ns-to-lib lib->deps (:src namespaces) entity-root-path user-home)
        test (lib-deps ws-dir ws-type test-deps package-test-deps package-name top-namespace ns-to-lib lib->deps (:test namespaces) entity-root-path user-home)]
    (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))
