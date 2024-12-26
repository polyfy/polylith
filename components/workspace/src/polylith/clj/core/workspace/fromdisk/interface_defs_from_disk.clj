(ns ^:no-doc polylith.clj.core.workspace.fromdisk.interface-defs-from-disk
  (:require
   [clojure.string :as str]
   [polylith.clj.core.file.interface :as file]
   [polylith.clj.core.workspace.fromdisk.definitions :as defs]))

(defn interface-ns? [namespace interface-name top-namespace interface-ns]
  (or (= namespace (str top-namespace "." interface-name "." interface-ns))
      (str/starts-with? namespace (str top-namespace "." interface-name "." interface-ns "."))))

(defn interface-from-disk [ws-dir ws-dialects top-namespace interface-name {:keys [namespace file-path]} interface-ns]
  (let [content (file/read-file (str ws-dir "/" file-path) ws-dialects)
        ns-name (str/replace-first namespace (str top-namespace "." interface-name ".") "")]
    (mapcat #(defs/definitions ns-name % interface-ns)
            (defs/filter-statements content))))

(defn arglist [arguments]
  (mapv :name arguments))

(defn defs-from-disk
  "Example of a src-dir: ./components/workspace-clj/src/polylith/clj/core/workspace_clj"
  [ws-dir ws-dialects top-namespace interface-name src-namespaces interface-ns]
  (->> src-namespaces
       (filter #(interface-ns? (:namespace %) interface-name top-namespace interface-ns))
       (mapcat #(interface-from-disk ws-dir ws-dialects top-namespace interface-name % interface-ns) )
       (sort-by (juxt :sub-ns :type :name arglist))
       (vec)))
