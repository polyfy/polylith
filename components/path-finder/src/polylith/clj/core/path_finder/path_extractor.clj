(ns polylith.clj.core.path-finder.path-extractor
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.str :as str-util]))

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

(defn path-entries [ws-dir paths profile? test?]
  (mapv #(path-entry ws-dir % profile? test?) paths))
