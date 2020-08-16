(ns polylith.clj.core.entity.path-extractor
  (:require [polylith.clj.core.util.interfc.str :as str-util]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]))

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

(defn path-entry [ws-dir path profile? test?]
  (let [name (entity-name path)
        main-dir (str-util/take-until path "/")
        type (main-dir->type main-dir :other)
        exists? (file/exists (str ws-dir "/" path))
        source-dir (source-dir path)]
    (util/ordered-map :name name
                      :type type
                      :profile? profile?
                      :test? test?
                      :source-dir source-dir
                      :exists? exists?
                      :path path)))

(defn select-path-entries [ws-dir paths profile? test?]
  (mapv #(path-entry ws-dir % profile? test?) paths))

(defn path-entries [ws-dir src-paths test-paths profile-src-paths profile-test-paths]
  (vec (concat (select-path-entries ws-dir src-paths false false)
               (select-path-entries ws-dir profile-src-paths true false)
               (select-path-entries ws-dir test-paths false true)
               (select-path-entries ws-dir profile-test-paths true true))))
