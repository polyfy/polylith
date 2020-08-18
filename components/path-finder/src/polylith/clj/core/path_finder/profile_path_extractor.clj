(ns polylith.clj.core.path-finder.profile-path-extractor
  (:require [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.path-finder.shared-path-extractor :as shared]))

(defn path-entry [ws-dir profile path test?]
  (let [{:keys [type name source-dir]} (shared/entity-type path)
        exists? (shared/exists? ws-dir path)]
    (util/ordered-map :profile profile
                      :name name
                      :type type
                      :test? test?
                      :source-dir source-dir
                      :exists? exists?
                      :path path)))

(defn path-entries [ws-dir profile paths test?]
  (mapv #(path-entry ws-dir profile % test?) paths))

(defn profile-entry [ws-dir [profile {:keys [paths test-paths]}]]
  (concat (path-entries ws-dir profile paths false)
          (path-entries ws-dir profile test-paths true)))

(defn profile-entries [ws-dir {:keys [profile->settings]}]
  (mapcat #(profile-entry ws-dir %) profile->settings))
