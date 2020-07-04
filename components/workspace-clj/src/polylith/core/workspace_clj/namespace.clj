(ns polylith.core.workspace-clj.namespace
  (:require [clojure.string :as str]
            [polylith.core.file.interfc :as file]
            [polylith.core.common.interfc :as common]
            [polylith.core.util.interfc.str :as str-util]))

(defn ->ns [ns-path]
  (str-util/skip-suffix (str/replace ns-path "/" ".") "."))

(defn top-src-dirs [top-namespaces]
  (mapv #(-> % key common/top-namespace common/ns-to-path) top-namespaces))

(defn matching-top-namespace [ns-path top-src-dir]
  (when (str/starts-with? ns-path top-src-dir)
    top-src-dir))

(defn matching-top-namespaces-for-path [ns-path top-src-dirs]
  (map #(matching-top-namespace ns-path %) top-src-dirs))

(defn top-brick-src-dirs
  ([ns-paths top-src-dirs]
   (vec (sort (set (filter identity (mapcat #(matching-top-namespaces-for-path % top-src-dirs) ns-paths))))))
  ([ws-path brick-type brick-name top-src-dirs]
   (let [brick-path (str ws-path "/" brick-type "s/" brick-name "/src/")
         paths (file/paths-recursively brick-path)
         ns-paths (map #(str-util/skip-prefix % brick-path) paths)]
     (top-brick-src-dirs ns-paths top-src-dirs))))
