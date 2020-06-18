(ns polylith.workspace-clj.namespaces-from-disk
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]
            [polylith.util.interface.str :as str-util]))

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

(defn namespace [root-dir file-path]
  (let [loc (file/number-of-lines file-path)
        content (file/read-file file-path)]
    {:name (namespace-name root-dir file-path)
     :loc loc
     :imports (filter-imports content)}))

(defn namespaces [root-dir]
  (let [file-paths (filter #(or (str/ends-with? % ".clj")
                                (str/ends-with? % ".cljc"))
                           (file/paths-recursively root-dir))]
    (mapv #(namespace root-dir %) file-paths)))
