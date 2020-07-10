(ns polylith.clj.core.workspace-clj.interface-defs-from-disk
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.workspace-clj.definitions :as defs]))

(defn interface-path [root-dir path]
  (subs path (-> root-dir count inc)))

(defn interface-ns? [path]
  (and (or (str/ends-with? path ".clj")
           (str/ends-with? path ".cljc"))
       (or (or (= "interfc.clj" path)
               (= "interfc.cljc" path)
               (= "interface.clj" path)
               (= "interface.cljc" path))
           (or (str/starts-with? path "interfc/")
               (str/starts-with? path "interface/")))))

(defn interface-ns [root-dir path]
  (let [index (str/index-of path ".")
        namespace (common/path-to-ns (subs path 0 index))]
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

(defn params [parameters]
  (mapv :name parameters))

(defn defs-from-disk [src-dir]
  "Example of a src-dir: ./components/common/src/polylith/common"
  (vec (sort-by (juxt :sub-ns :type :name params)
                (mapcat interface-from-disk
                        (interface-namespaces src-dir)))))
