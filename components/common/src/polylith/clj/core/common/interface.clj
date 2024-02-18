(ns ^:no-doc polylith.clj.core.common.interface
  (:require [polylith.clj.core.common.class-loader :as class-loader]
            [polylith.clj.core.common.core :as core]
            [polylith.clj.core.common.file-output :as file-output]
            [polylith.clj.core.common.ns-extractor :as ns-extractor]
            [polylith.clj.core.common.profile :as profile]
            [polylith.clj.core.common.validate-args :as validate-args]
            [polylith.clj.core.version.interface :as version]))

(def entity->short core/entity->short)
(def entity->long core/entity->long)

(defn absolute-path [path entity-root-path]
  (core/absolute-path path entity-root-path))

(defn brick-names-to-test [test all-brick-names]
  (core/brick-names-to-test test all-brick-names))

(defn calculate-latest-version? [user-input]
  (core/calculate-latest-version? user-input))

(defn color-mode [user-input]
  (core/color-mode user-input))

(defn compact? [workspace view]
  (core/compact? workspace view))

(defn create-class-loader [paths color-mode]
  (class-loader/create-class-loader paths color-mode))

(defn eval-in [class-loader form]
  (class-loader/eval-in class-loader form))

(defn entity-imports [entity & sources]
  (ns-extractor/entity-imports entity sources))

(defn entity-namespaces [entity & sources]
  (ns-extractor/entity-namespaces entity sources))

(defn entities-namespaces [entities & sources]
  (ns-extractor/entities-namespaces entities sources))

(defn extract-namespace [suffixed-top-ns ns-to-extract]
  (ns-extractor/extract suffixed-top-ns ns-to-extract))

(defn filter-clojure-paths [paths]
  (core/filter-clojure-paths paths))

(defn find-base [base-name bases]
  (core/find-base base-name bases))

(defn find-brick [name workspace]
  (core/find-brick name workspace))

(defn find-component [name components]
  (core/find-component name components))

(defn find-project [project-name-or-alias projects]
  (core/find-project project-name-or-alias projects))

(defn find-entity-index [entity-name entities]
  (core/find-entity-index entity-name entities))

(defn ns-to-path [namespace]
  (core/ns-to-path namespace))

(defn path-to-ns [namespace]
  (when namespace
    (core/path-to-ns namespace)))

(defn suffix-ns-with-dot [namespace]
  (core/sufix-ns-with-dot namespace))

(defn validate-args [unnamed-args example]
  (validate-args/validate unnamed-args example))

(defn toolsdeps1? [workspace]
  (= :toolsdeps1 (-> workspace :version :from :ws :type)))

(defn version-name [fake-poly?]
  (if fake-poly?
    (str "poly " version/name-without-rev)
    (str version/tool " " version/name
         (if version/snapshot?
           (str " #" version/snapshot)
           ""))))

(defn user-path [path]
  (core/user-path path))

(defn invalid-workspace? [workspace]
  (core/invalid-workspace? workspace))

(defn interface-nss [interface-ns]
  (set ["ifc" "interface" interface-ns]))

(defn interface-ns? [namespace interface-ns]
  (contains? (interface-nss interface-ns)
             namespace))

(defn sort-profiles [default-profile-name profiles]
  (profile/sort-profiles default-profile-name profiles))

(defn print-or-save-table
  ([workspace table-fn]
   (file-output/print-or-save-table workspace table-fn nil nil))
  ([workspace table-fn canvas-areas post-print-fn]
   (file-output/print-or-save-table workspace table-fn canvas-areas post-print-fn)))
