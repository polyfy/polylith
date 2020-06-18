(ns polylith.workspace-clj.imports-from-disk
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]
            [polylith.util.interface.str :as str-util]))

(defn require? [val]
  (and (sequential? val)
       (= :require (first val))))

(defn ->imports [imports]
  (rest (first (filter require? imports))))

(defn filter-imports [content]
  (vec (sort (map #(-> % first str)
                  (filterv #(= :as (second %))
                           (->imports (first content)))))))

(defn ->namespace [root-dir path]
  (-> (subs path (count root-dir))
      (str-util/skip-until "/")
      (str-util/skip-suffixes [".clj" ".cljc"])
      (str/replace "/" ".")
      (str/replace "_" "-")))

(defn imports [root-dir file-path]
  (let [content (file/read-file file-path)]
    {:name (->namespace root-dir file-path)
     :imports (filter-imports content)}))

(defn namespaces [root-dir]
  (let [file-paths (filter #(or (str/ends-with? % ".clj")
                                (str/ends-with? % ".cljc"))
                           (file/paths-recursively root-dir))]
    (mapv #(imports root-dir %) file-paths)))
