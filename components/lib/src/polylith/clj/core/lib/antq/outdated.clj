(ns ^:no-doc polylith.clj.core.lib.antq.outdated)

(defn update? [entity-name lib libs entity-settings]
  (and (or (empty? libs)
           (contains? libs lib))
       (not (contains? (set (get-in entity-settings [entity-name :keep-lib-versions]))
                       (symbol lib)))))

(defn update-lib-version? [entity-name entity-type lib libs {:keys [bricks projects]}]
  (if (= "project" entity-type)
    (update? entity-name lib libs projects)
    (update? entity-name lib libs bricks)))

(defn outdated-libraries [library->latest-version]
  (set (map #(-> % ffirst)
            library->latest-version)))

(defn source-with-lib [[lib v] entity-name entity-type outdated-libs lib->latest-version libs settings]
  (if (and (contains? outdated-libs lib)
           (update-lib-version? entity-name entity-type lib libs settings))
    [lib (assoc v :latest-version
                  (lib->latest-version [lib (:version v)]))]
    [lib v]))

(defn source-with-libs [deps entity-name entity-type outdated-libs lib->latest-version libs settings]
  (into {} (map #(source-with-lib % entity-name entity-type outdated-libs lib->latest-version libs settings)
                deps)))

(defn lib-deps-with-latest-version [{:keys [src test] :as lib-deps} entity-name entity-type outdated-libs lib->latest-version {:keys [libraries]} settings]
  (let [libs (set libraries)]
    (cond-> lib-deps
      src (assoc :src (source-with-libs src entity-name entity-type outdated-libs lib->latest-version libs settings))
      test (assoc :test (source-with-libs test entity-name entity-type outdated-libs lib->latest-version libs settings)))))
