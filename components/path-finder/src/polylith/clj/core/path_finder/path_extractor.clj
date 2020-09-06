(ns polylith.clj.core.path-finder.path-extractor
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.path-finder.profile-src-splitter :as profile-src-splitter]))

(def dir->type {"bases" :base
                "components" :component
                "environments" :environment})

(defn source-dir [path]
  (-> path
      (str-util/skip-until "/")
      (str-util/skip-until "/")))

(defn entity-name [path]
  (-> path
      (str-util/skip-until "/")
      (str-util/take-until "/")))

(defn main-dir [path]
  (str-util/take-until path "/"))

(defn exists? [ws-dir path]
  (file/exists (str ws-dir "/" path)))

(defn entity-type [dir type name path]
  (if type
    {:name name
     :type type
     :source-dir (source-dir path)}
    (if (= dir "development")
      {:name dir
       :type :environment
       :source-dir (str-util/skip-until path "/")}
      {:name name
       :type :other
       :source-dir (source-dir path)})))

(defn path-entry [ws-dir path profile? test?]
  (let [dir (main-dir path)
        type (dir->type dir)
        entity-name (entity-name path)
        {:keys [name type source-dir]} (entity-type dir type entity-name path)
        exists? (exists? ws-dir path)]
    (util/ordered-map :name name
                      :type type
                      :profile? profile?
                      :test? test?
                      :source-dir source-dir
                      :exists? exists?
                      :path path)))

(defn single-path-entries [ws-dir paths profile? test?]
  (when paths (mapv #(path-entry ws-dir % profile? test?) paths)))

(defn path-entries [ws-dir src-paths test-paths profile-src-paths profile-test-paths]
  (vec (concat (single-path-entries ws-dir src-paths false false)
               (single-path-entries ws-dir test-paths false true)
               (single-path-entries ws-dir profile-src-paths true false)
               (single-path-entries ws-dir profile-test-paths true true))))

(defn from-unenriched-environment [ws-dir dev? src-paths test-paths settings user-input]
  (let [{:keys [profile-src-paths profile-test-paths]} (profile-src-splitter/extract-active-dev-profiles-paths dev? settings user-input)]
    (path-entries ws-dir src-paths test-paths profile-src-paths profile-test-paths)))

(defn from-profiles-paths [ws-dir settings profile-name]
  (let [{:keys [src-paths test-paths]} (profile-src-splitter/extract-profile-paths profile-name settings)]
    (path-entries ws-dir src-paths test-paths nil nil)))
