(ns polylith.clj.core.workspace-clj.namespaces-from-disk
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.str :as str-util])
  (:refer-clojure :exclude [import require]))

;; (:require ,,,) handling

;; Borrowed from `clojure.core`, where it's a private fn.
(defn libspec?
  "Returns true if x is a libspec."
  [x]
  (or (symbol? x)
      (and (vector? x)
           (or
            (nil? (second x))
            (keyword? (second x))))))

(defn libspec->lib
  "Given a valid libspec, return the lib it's specifying."
  [libspec]
  (if (symbol? libspec)
    libspec
    (first libspec)))

(defn prefix-list->lib-strs
  "Given a valid prefix list, return the libs they specify as strings."
  [[prefix & libspecs]]
  (map #(str prefix \.
             (libspec->lib %))
       libspecs))

;; (:import ,,,) handling

(defn import-list->package-str
  "Given an import-list, as handled by `clojure.core/import`, return the
  package name as a string."
  [import-list]
  (if (symbol? import-list)
    (->> import-list
         str
         (re-find #"(.*)\.\w+$")
         last)
    (-> import-list
        first
        str)))

;; import/require handling

(defn import? [statement]
  (and
    (sequential? statement)
    (contains? #{:import :require} (first statement))))

(defn import [[statement-type & statement-body]]
  (cond
    (= :require statement-type)
    (flatten
      (concat (map (comp str libspec->lib)
                   (filter libspec? statement-body))
              (map prefix-list->lib-strs
                   (filter sequential?
                           (remove libspec?
                                   statement-body)))))
    (= :import statement-type)
    (map import-list->package-str
         statement-body)))

(defn imports [ns-statements]
  (vec (sort (mapcat import (filterv import? ns-statements)))))

(defn skip-slash [path]
  (or (str-util/skip-until path "/")
      path))

(defn namespace-name [root-dir path]
  (when path
    (when-let [file-path (-> (subs path (count root-dir))
                             (skip-slash))]
      (-> file-path
          (str-util/skip-suffixes [".clj" ".cljc"])
          (str/replace "/" ".")
          (str/replace "_" "-")))))

(defn ->namespace [source-dir file-path]
  (let [content (file/read-first-statement file-path)]
    {:name (namespace-name source-dir file-path)
     :namespace (-> content second str)
     :file-path file-path
     :imports (-> content imports)}))

(defn source-namespaces-from-disk [source-dir]
  (mapv #(->namespace source-dir %)
        (-> source-dir
            file/paths-recursively
            common/filter-clojure-paths)))

(defn namespaces-from-disk [src-dirs test-dirs]
  (let [src (vec (mapcat #(source-namespaces-from-disk %)
                         src-dirs))
        test (vec (mapcat #(source-namespaces-from-disk %)
                          test-dirs))]
    (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))
