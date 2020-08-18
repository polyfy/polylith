(ns polylith.clj.core.path-finder.dep-extractor
  (:require [polylith.clj.core.util.interfc :as util]))

(defn deps-entry [dep profile? test?]
  (util/ordered-map :profile? profile?
                    :test? test?
                    :dep dep))

(defn select-dep-entries [deps profile? test?]
  (mapv #(deps-entry % profile? test?) deps))

(defn dep-entries [src-deps test-deps profile-deps]
  (vec (concat (select-dep-entries src-deps false false)
               (select-dep-entries test-deps false true)
               (select-dep-entries profile-deps true false))))
