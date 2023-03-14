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
  [src-dirs interface-ns]
  (->> src-dirs
       (into []
             (comp
               (mapcat #(interface-namespaces % interface-ns))
               (mapcat #(interface-from-disk % interface-ns))))
       (sort-by (juxt :sub-ns :type :name params))
       (vec)))

(comment
  ;; This returns '() right now, but should return:
  ;;   '((ns com.for.test.company.interface) (def abc 123))
  ;;
  ;; If we comment out the :require statement from the interface.cljc file, then it works.
  ;;
  ;; Can also be tested by executing "poly ws get:components:company:interface"
  ;; from the examples/for-test directory, which should return:
  ;;   {:definitions [{:name "abc", :type "data"}], :name "company"}
  (file/read-file "examples/for-test/components/company/cljc/com/for/test/company/interface.cljc")
  #__)
