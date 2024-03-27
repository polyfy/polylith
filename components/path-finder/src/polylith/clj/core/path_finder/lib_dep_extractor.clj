(ns ^:no-doc polylith.clj.core.path-finder.lib-dep-extractor
  (:require [polylith.clj.core.util.interface :as util]))

(defn deps-entry [lib-dep profile? test?]
  (util/ordered-map :profile? profile?
                    :test? test?
                    :lib-dep lib-dep))

(defn select-dep-entries [deps profile? test?]
  (mapv #(deps-entry % profile? test?) deps))

(defn extract-profile-deps [is-dev profiles {:keys [active-profiles]}]
  (if is-dev (apply merge (map :lib-deps
                               (filter #(contains? active-profiles (:name %))
                                       profiles)))
           {}))

(defn from-library-deps [is-dev {:keys [src test]} profiles settings]
  (let [profile-deps (extract-profile-deps is-dev profiles settings)]
    (vec (concat (select-dep-entries src false false)
                 (select-dep-entries test false true)
                 (select-dep-entries profile-deps true false)))))
