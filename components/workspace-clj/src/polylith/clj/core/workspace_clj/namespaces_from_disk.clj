(ns polylith.clj.core.workspace-clj.namespaces-from-disk
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc.str :as str-util]
            [polylith.clj.core.common.interfc :as common])
  (:refer-clojure :exclude [import]))

(defn import? [statement]
  (and
    (sequential? statement)
    (= :import (first statement))))

(defn import [statement]
  (map #(-> % first str) (rest statement)))

(defn imported-namespaces [ns-statements]
  (mapcat import (filterv import? ns-statements)))

(defn require? [statement]
  (and
    (sequential? statement)
    (= :require (first statement))))

(defn require-statements [ns-statements]
  (rest (first (filter require? ns-statements))))

(defn required-namespaces [ns-statements]
  (vec (sort (map #(-> % first str)
                  (filterv #(= :as (second %))
                           (require-statements ns-statements))))))

(defn imports [ns-statement]
  (concat (imported-namespaces ns-statement)
          (required-namespaces ns-statement)))

(defn namespace-name [root-dir path]
  (when path
    (when-let [file-path (-> (subs path (count root-dir))
                             (str-util/skip-until "/"))]
      (-> file-path
          (str-util/skip-suffixes [".clj" ".cljc"])
          (str/replace "/" ".")
          (str/replace "_" "-")))))

(defn ->namespace [root-dir file-path]
  (let [content (file/read-file file-path)]
    {:name (namespace-name root-dir file-path)
     :namespace (-> content first second str)
     :file-path file-path
     :imports (-> content first imports)}))

(defn namespaces-from-disk [root-dir]
  (mapv #(->namespace root-dir %)
        (-> root-dir
            file/paths-recursively
            common/filter-clojure-paths)))
