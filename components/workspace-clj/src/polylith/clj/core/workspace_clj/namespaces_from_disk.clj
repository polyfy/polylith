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

(defn import [[statement-type & statement-body]]
  (map #(cond
          ;; TODO make `sequential?`
          (seqable? %)
          (-> % first str)

          (= :require statement-type)
          (str %)

          (= :import statement-type)
          (->> %
               str
               (re-find #"(.*)\.\w+$")
               last))
       statement-body))

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
