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
    {:ns namespace
     :path (str root-dir "/" path)}))

(defn interface-namespaces [brick-src-ws-dir]
  (let [paths (filterv interface-ns?
                       (map #(interface-path brick-src-ws-dir %)
                            (file/paths-recursively brick-src-ws-dir)))]
    (vec (sort-by (juxt :ns :type :name :parameters)
                  (mapv #(interface-ns brick-src-ws-dir %) paths)))))

(defn interface-from-disk [{:keys [ns path]}]
  (let [content (file/read-file path)
        statements (defs/filter-statements content)]
    (vec (sort-by (juxt :ns :type :name :parameters)
                  (mapcat #(defs/->definitions ns %) statements)))))

(defn defs-from-disk [src-dir]
  (mapcat interface-from-disk
          (interface-namespaces src-dir)))
