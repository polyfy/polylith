(ns polylith.workspace-clj.namespaces-from-disk
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]
            [polylith.util.interface.str :as str-util]
            [polylith.common.interface :as common]))

(defn require? [val]
  (and (sequential? val)
       (= :require (first val))))

(defn imports [imports]
  (rest (first (filter require? imports))))

(defn filter-imports [content]
  (vec (sort (map #(-> % first str)
                  (filterv #(= :as (second %))
                           (imports (first content)))))))

(defn namespace-name [root-dir path]
  (-> (subs path (count root-dir))
      (str-util/skip-until "/")
      (str-util/skip-suffixes [".clj" ".cljc"])
      (str/replace "/" ".")
      (str/replace "_" "-")))

(defn ->namespace [root-dir file-path]
  (let [content (file/read-file file-path)]
    {:name (namespace-name root-dir file-path)
     :file-path file-path
     :imports (filter-imports content)}))

(defn namespaces-from-disk [root-dir]
  (mapv #(->namespace root-dir %)
        (-> root-dir
            file/paths-recursively
            common/filter-clojure-paths)))
