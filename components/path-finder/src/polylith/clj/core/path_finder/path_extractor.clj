(ns polylith.clj.core.path-finder.path-extractor
  (:require [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.path-finder.shared-path-extractor :as shared]))

(defn path-entry [ws-dir path profile? test?]
  (let [{:keys [type name source-dir]} (shared/entity-type path)
        exists? (shared/exists? ws-dir path)]
    (util/ordered-map :name name
                      :type type
                      :profile? profile?
                      :test? test?
                      :source-dir source-dir
                      :exists? exists?
                      :path path)))

(defn path-entries [ws-dir paths profile? test?]
  (mapv #(path-entry ws-dir % profile? test?) paths))
