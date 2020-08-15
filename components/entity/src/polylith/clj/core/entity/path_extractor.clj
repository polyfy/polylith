(ns polylith.clj.core.entity.path-extractor
  (:require [polylith.clj.core.util.str :as str-util]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]))

(def main-dir->category {"bases" :brick
                         "components" :brick
                         "environments" :env})

(def main-dir->type {"bases" :base
                     "components" :component
                     "environments" :environment})

(defn entity-name [path]
  (-> path
      (str-util/skip-until "/")
      (str-util/take-until "/")))

(defn source-dir [path]
  (-> path
      (str-util/skip-until "/")
      (str-util/skip-until "/")))

(defn info-path [ws-dir path profile? test?]
  (let [name (entity-name path)
        main-dir (str-util/take-until path "/")
        category (main-dir->category main-dir :other)
        type (main-dir->type main-dir)
        src-or-test (if test? :test :src)
        exists? (file/exists (str ws-dir "/" path))
        source-dir (source-dir path)]
    [[category name src-or-test]
     (util/ordered-map :name name
                       :type type
                       :profile? profile?
                       :test? test?
                       :source-dir source-dir
                       :exists? exists?
                       :path path)]))

(defn ->info-paths [ws-dir paths profile? test?]
  (mapv #(info-path ws-dir % profile? test?) paths))

(defn extract-group [[k v]]
  [k (mapv second v)])

(defn path-infos [ws-dir src-paths test-paths profile-src-paths profile-test-paths]
  (let [info-paths (apply concat [(->info-paths ws-dir src-paths false false)
                                  (->info-paths ws-dir profile-src-paths true false)
                                  (->info-paths ws-dir test-paths false true)
                                  (->info-paths ws-dir profile-test-paths true true)])
        grouped-info-paths (filterv first (group-by first info-paths))]
    (into {} (mapv extract-group grouped-info-paths))))
