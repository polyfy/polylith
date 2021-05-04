(ns polylith.clj.core.workspace-clj.namespaces-from-disk
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.str :as str-util])
  (:refer-clojure :exclude [import require]))

(defn import? [statement]
  (and
    (sequential? statement)
    (contains? #{:import :require} (first statement))))

;; (:require ,,,) handling

(defn libspec?
  "Returns true if x is a libspec."
  [x]
  (or (symbol? x)
      (and (vector? x)
           (or
            (nil? (second x))
            (keyword? (second x))))))

(defn libspec->lib
  [libspec]
  (if (symbol? libspec)
    libspec
    (first libspec)))

(defn prefix-list->libs
  [[prefix & libspecs]]
  (map #(str prefix \.
             (libspec->lib %))
       libspecs))

(defn import [[statement-type & statement-body]]
  (cond
    (= :require statement-type)
    (flatten
     (concat (map (comp str libspec->lib)
                  (filter libspec? statement-body))
             (map prefix-list->libs
                  (remove libspec? statement-body))))

    (= :import statement-type)
    (map #(->> %
               str
               (re-find #"(.*)\.\w+$")
               last)
         statement-body)))

(defn imports [ns-statements]
  (vec (sort (mapcat import (filterv import? ns-statements)))))

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
