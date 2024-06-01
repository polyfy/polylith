(ns ^:no-doc polylith.clj.core.lib.antq.outdated)

(defn library-selected-for-update?
  "Check if the library is in the set of libraries that we want to update,
  if passed in by libraries:LIB1:LIB2. If not passed in, it's empty,
  and we consider it as selected."
  [lib-name selected-libs]
  (or (empty? selected-libs)
      (contains? selected-libs lib-name)))

(defn old-library-version?
  "We only need to update a library if it's not the latest version."
  [{:keys [version]} lib-name lib->latest-version]
  (not= version
        (lib->latest-version lib-name)))

(defn update-library?
  "We can specify :keep-lib-versions in :bricks and :projects in workspace.edn.
   If the library doesn't exist in that vector for a brick or project, then
   it's fine to update the library."
  [lib-name entity-name entity-type name-type->keep-lib-versions]
  (not (contains? (get name-type->keep-lib-versions [entity-name entity-type])
                  lib-name)))

(defn update-lib-version? [entity-name entity-type lib-name lib-def selected-libs lib->latest-version name-type->keep-lib-versions]
  (and (library-selected-for-update? lib-name selected-libs)
       (old-library-version? lib-def lib-name lib->latest-version)
       (update-library? lib-name entity-name entity-type name-type->keep-lib-versions)))

(defn outdated-libraries [library->latest-version]
  (set (keys library->latest-version)))

(defn source-with-lib [[lib-name lib-def] entity-name entity-type outdated-libs lib->latest-version selected-libs name-type->keep-lib-versions]
  (if (and (contains? outdated-libs lib-name)
           (update-lib-version? entity-name entity-type lib-name lib-def selected-libs lib->latest-version name-type->keep-lib-versions))
    [lib-name (assoc lib-def :latest-version
                             (lib->latest-version lib-name))]
    [lib-name lib-def]))

(defn source-with-libs [deps entity-name entity-type outdated-libs lib->latest-version selected-libs name-type->keep-lib-versions]
  (into {} (map #(source-with-lib % entity-name entity-type outdated-libs lib->latest-version selected-libs name-type->keep-lib-versions)
                deps)))

(defn lib-deps-with-latest-version [{:keys [src test] :as lib-deps} entity-name entity-type outdated-libs lib->latest-version {:keys [libraries]} name-type->keep-lib-versions]
  (let [selected-libs (set libraries)]
    (cond-> lib-deps
            src (assoc :src (source-with-libs src entity-name entity-type outdated-libs lib->latest-version selected-libs name-type->keep-lib-versions))
            test (assoc :test (source-with-libs test entity-name entity-type outdated-libs lib->latest-version selected-libs name-type->keep-lib-versions)))))
