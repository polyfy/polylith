(ns polylith.workspace-clj.imports-from-disk
  (:require [polylith.file.interface :as file]
            [clojure.string :as str]))

(defn require? [val]
  (and (sequential? val)
       (= :require (first val))))

(defn ->imports [imports]
  (rest (first (filter require? imports))))

(defn filter-imports [content]
  (vec (sort (map #(-> % first str)
                  (filterv #(= :as (second %))
                           (->imports (first content)))))))

(defn short-path [root-dir path]
  (subs path (count root-dir)))

(defn imports [root-dir file-path]
  (let [content (file/read-file file-path)]
    {:ns-path (short-path root-dir file-path)
     :imports (filter-imports content)}))

(defn namespaces [root-dir]
  (let [file-paths (filter #(or (str/ends-with? % ".clj")
                                (str/ends-with? % ".cljc"))
                           (file/paths-recursively root-dir))]
    (mapv #(imports root-dir %) file-paths)))
