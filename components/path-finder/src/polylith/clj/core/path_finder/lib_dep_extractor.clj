(ns polylith.clj.core.path-finder.lib-dep-extractor
  (:require [polylith.clj.core.util.interfc :as util]))

(defn deps-entry [lib-dep profile? test?]
  (util/ordered-map :profile? profile?
                    :test? test?
                    :lib-dep lib-dep))

(defn select-dep-entries [deps profile? test?]
  (mapv #(deps-entry % profile? test?) deps))

(defn extract-deps [dev? {:keys [profile->settings]} {:keys [active-dev-profiles]}]
  (if dev? (apply merge (map :lib-deps (map #(profile->settings %) active-dev-profiles)))
           {}))

(defn lib-dep-entries [dev? src-deps test-deps settings user-input]
  (let [profile-deps (extract-deps dev? settings user-input)]
    (vec (concat (select-dep-entries src-deps false false)
                 (select-dep-entries test-deps false true)
                 (select-dep-entries profile-deps true false)))))
