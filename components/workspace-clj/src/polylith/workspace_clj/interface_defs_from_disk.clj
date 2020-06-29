(ns polylith.workspace-clj.interface-defs-from-disk
  (:require [polylith.file.interface :as file]
            [polylith.workspace-clj.definitions :as defs]
            [clojure.string :as str]))

(defn interface-path [root-dir path]
  (subs path (-> root-dir count inc)))

(defn interface-ns? [path]
  (and (or (str/ends-with? path ".clj")
           (str/ends-with? path ".cljc"))
       (or (or (= "interface.clj" path)
               (= "interface.cljc" path))
           (str/starts-with? path "interface/"))))

(defn interface-ns [root-dir path]
  (let [index (str/index-of path ".")
        namespace (str/replace (subs path 0 index) "/" ".")]
    {:sub-ns namespace
     :path (str root-dir "/" path)}))

(defn interface-namespaces [src-dir]
  (let [paths (filterv interface-ns?
                       (map #(interface-path src-dir %)
                            (file/paths-recursively src-dir)))]
    (mapv #(interface-ns src-dir %) paths)))

(defn interface-from-disk [{:keys [sub-ns path]}]
  (let [content (file/read-file path)
        statements (defs/filter-statements content)]
    (mapcat #(defs/definitions sub-ns %) statements)))

(defn defs-from-disk [src-dir]
  "Example of a src-dir: ./components/common/src/polylith/common"
  (vec (sort-by (juxt :sub-ns :type :name :parameters)
                (mapcat interface-from-disk
                        (interface-namespaces src-dir)))))
