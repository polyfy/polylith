(ns polylith.clj.core.lib.outdated)

;; todo: filter out ignored libraries.
(defn outdated-libraries [library->latest-version]
  (set (map #(-> % ffirst)
            library->latest-version)))

(defn source-with-lib [[k v] outdated-libs lib->latest-version]
  (if (contains? outdated-libs k)
    [k (assoc v :latest-version
                (lib->latest-version [k (:version v)]))]
    [k v]))

(defn source-with-libs [deps outdated-libs lib->latest-version]
  (into {} (map #(source-with-lib % outdated-libs lib->latest-version)
                deps)))

(defn lib-deps-with-latest-version [{:keys [src test] :as lib-deps} outdated-libs lib->latest-version]
  (cond-> lib-deps
          src (assoc :src (source-with-libs src outdated-libs lib->latest-version))
          test (assoc :test (source-with-libs test outdated-libs lib->latest-version))))
