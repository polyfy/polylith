(ns polylith.workspace-kotlin.namespaces-from-disk
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]
            [polylith.workspace-kotlin.indexedrows :as irows]))

(defn the-name [[_ row]]
  (second (str/split (str/trim row) #" ")))

(defn object-name [indexed-rows]
  (the-name (irows/find-first-that-contains indexed-rows 0 "object ")))

(defn package-name [indexed-rows]
  (the-name (irows/find-first-that-starts-with indexed-rows "package ")))

(defn imports [indexed-rows]
  (mapv the-name (irows/filter-matched indexed-rows "import ")))

(defn ->namespace [file-path]
  (let [content (slurp file-path)
        indexed-rows (map-indexed vector (str/split-lines content))
        name (object-name indexed-rows)]
    {:name name
     :namespace (package-name indexed-rows)
     :file-path file-path
     :imports (imports indexed-rows)}))

(defn filter-kotlin-files [paths]
  (filterv #(str/ends-with? % ".kt")
           paths))

(defn namespaces-from-disk [root-dir]
  (mapv ->namespace
        (-> root-dir
            file/paths-recursively
            filter-kotlin-files)))
