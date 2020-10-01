(ns polylith.clj.core.path-finder.lib-dep-extractor
  (:require [polylith.clj.core.util.interface :as util]))

(defn deps-entry [lib-dep profile? test?]
  (util/ordered-map :profile? profile?
                    :test? test?
                    :lib-dep lib-dep))

(defn select-dep-entries [deps profile? test?]
  (mapv #(deps-entry % profile? test?) deps))

(defn extract-deps [is-dev {:keys [profile-to-settings active-profiles]}]
  (if is-dev (apply merge (map :lib-deps (map #(profile-to-settings %) active-profiles)))
           {}))

(defn from-library-deps [is-dev src-deps test-deps settings]
  (let [profile-deps (extract-deps is-dev settings)]
    (vec (concat (select-dep-entries src-deps false false)
                 (select-dep-entries test-deps false true)
                 (select-dep-entries profile-deps true false)))))
