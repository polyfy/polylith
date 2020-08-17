(ns polylith.clj.core.entity.path-extractor
  (:require [polylith.clj.core.util.interfc.str :as str-util]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]))

(def dir->type {"bases" :base
                "components" :component
                "environments" :environment})

(defn source-dir [path]
  (-> path
      (str-util/skip-until "/")
      (str-util/skip-until "/")))

(defn entity-type [name dir path]
  (if-let [type (dir->type dir)]
    {:name name
     :type type
     :source-dir (source-dir path)}
    (if (= dir "development")
      {:name dir
       :type :environment
       :source-dir name}
      {:name name
       :type :other
       :source-dir (source-dir path)})))

(defn entity-name [path]
  (-> path
      (str-util/skip-until "/")
      (str-util/take-until "/")))

(defn path-entry [ws-dir path profile? test?]
  (let [entity-name (entity-name path)
        main-dir (str-util/take-until path "/")
        {:keys [type name source-dir]} (entity-type entity-name main-dir path)
        exists? (file/exists (str ws-dir "/" path))]
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
