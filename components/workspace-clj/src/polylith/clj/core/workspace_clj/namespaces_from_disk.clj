(ns polylith.clj.core.workspace-clj.namespaces-from-disk
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc.str :as str-util]
            [polylith.clj.core.common.interfc :as common]))

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
  (when path
    (let [file-path (-> (subs path (count root-dir))
                        (str-util/skip-until "/"))]
      (when file-path
        (-> file-path
            (str-util/skip-suffixes [".clj" ".cljc"])
            (str/replace "/" ".")
            (str/replace "_" "-"))))))

(defn ->namespace [root-dir file-path]
  (let [content (file/read-file file-path)]
    {:name (namespace-name root-dir file-path)
     :namespace (-> content first second str) ; TODO: discuss with Jocke
     :file-path file-path
     :imports (filter-imports content)}))

(defn namespaces-from-disk [root-dir]
  (mapv #(->namespace root-dir %)
        (-> root-dir
            file/paths-recursively
            common/filter-clojure-paths)))
