(ns polylith.clj.core.path-finder.shared-path-extractor
  (:require [polylith.clj.core.util.interfc.str :as str-util]
            [polylith.clj.core.file.interfc :as file]))

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

(defn entity-type [path]
  (let [dir (main-dir path)
        type (dir->type dir)
        name (entity-name path)]
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
         :source-dir (source-dir path)}))))
