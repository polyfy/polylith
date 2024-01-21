(ns ^:no-doc polylith.clj.core.lib.antq.outdated)

(defn update-lib-version? [entity-name entity-type lib libs name-type->keep-lib-version]
  (and (or (empty? libs)
           (contains? libs lib))
       (not (contains? (set (get name-type->keep-lib-version [entity-name entity-type]))
                       lib))))

(defn outdated-libraries [library->latest-version]
  (set (keys library->latest-version)))

(defn source-with-lib [[lib v] entity-name entity-type outdated-libs lib->latest-version libs name-type->keep-lib-version]
  (if (and (contains? outdated-libs lib)
           (update-lib-version? entity-name entity-type lib libs name-type->keep-lib-version))
    [lib (assoc v :latest-version
                  (lib->latest-version lib))]
    [lib v]))

(defn source-with-libs [deps entity-name entity-type outdated-libs lib->latest-version libs name-type->keep-lib-version]
  (into {} (map #(source-with-lib % entity-name entity-type outdated-libs lib->latest-version libs name-type->keep-lib-version)
                deps)))

(defn lib-deps-with-latest-version [{:keys [src test] :as lib-deps} entity-name entity-type outdated-libs lib->latest-version {:keys [libraries]} name-type->keep-lib-version]
  (let [libs (set libraries)]
    (cond-> lib-deps
      src (assoc :src (source-with-libs src entity-name entity-type outdated-libs lib->latest-version libs name-type->keep-lib-version))
      test (assoc :test (source-with-libs test entity-name entity-type outdated-libs lib->latest-version libs name-type->keep-lib-version)))))
