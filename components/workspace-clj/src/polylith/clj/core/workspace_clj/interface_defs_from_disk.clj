(ns polylith.clj.core.workspace-clj.interface-defs-from-disk
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.workspace-clj.definitions :as defs]))

(defn interface-path [root-dir path]
  (subs path (-> root-dir count inc)))

(defn interface-ns? [path interface-ns]
  (and (or (str/ends-with? path ".clj")
           (str/ends-with? path ".cljc"))
       (or (or (= path (str interface-ns ".clj"))
               (= path (str interface-ns ".cljc")))
           (str/starts-with? path (str interface-ns "/")))))

(defn ->interface-ns [root-dir path]
  (let [index (str/index-of path ".")
        namespace (common/path-to-ns (subs path 0 index))]
    {:sub-ns namespace
     :path (str root-dir "/" path)}))

(defn interface-namespaces [src-dir interface-ns]
  (let [paths (filterv #(interface-ns? % interface-ns)
                       (map #(interface-path src-dir %)
                            (file/paths-recursively src-dir)))]
    (mapv #(->interface-ns src-dir %) paths)))

(defn interface-from-disk [{:keys [sub-ns path]} interface-ns]
  (let [content (file/read-file path)
        statements (defs/filter-statements content)]
    (mapcat #(defs/definitions sub-ns % interface-ns) statements)))

(defn params [parameters]
  (mapv :name parameters))

(defn defs-from-disk
  "Example of a src-dir: ./components/workspace-clj/src/polylith/clj/core/workspace_clj"
  [src-dir interface-ns]
  (vec (sort-by (juxt :sub-ns :type :name params)
                (mapcat #(interface-from-disk % interface-ns)
                        (interface-namespaces src-dir interface-ns)))))
