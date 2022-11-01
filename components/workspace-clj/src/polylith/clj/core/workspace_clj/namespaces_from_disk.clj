(ns polylith.clj.core.workspace-clj.namespaces-from-disk
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.str :as str-util])
  (:refer-clojure :exclude [import require]))

;; (:require ,,,) handling

;; Borrowed from `clojure.core`, where it's a private fn.
(defn libspec?
  "Returns true if x is a libspec."
  [x]
  (or (symbol? x)
      (and (vector? x)
           (or
             (nil? (second x))
             (keyword? (second x))))))

(defn libspec->lib
  "Given a valid libspec, return the lib it's specifying."
  [libspec]
  (if (symbol? libspec)
    libspec
    (first libspec)))

(defn prefix-list->lib-strs
  "Given a valid prefix list, return the libs they specify as strings."
  [[prefix & libspecs]]
  (map #(str prefix \.
             (libspec->lib %))
       libspecs))

;; (:import ,,,) handling

(defn import-list->package-str
  "Given an import-list, as handled by `clojure.core/import`, return the
  package name as a string."
  [import-list]
  (if (symbol? import-list)
    (->> import-list
         str
         (re-find #"(.*)\.\w+$")
         last)
    (-> import-list
        first
        str)))

;; import/require handling

(defn import? [statement]
  (and
    (sequential? statement)
    (contains? #{:import :require} (first statement))))

(defn interface-ns? [ns-name interface-ns]
  (and
    (-> ns-name nil? not)
    (or (= interface-ns ns-name)
        (str/starts-with? ns-name (str interface-ns ".")))))

; [required-ns as]

(defn required-as? [x suffixed-top-ns interface-ns]
  (when (libspec? x)
    (let [required-ns (cond (symbol? x) x
                            (vector? x) (first x))
          as (when (vector? x) (second x))
          {:keys [depends-on-ns]} (common/extract-namespace suffixed-top-ns (str required-ns))]
      (not (and (= :as-alias as)
                (interface-ns? depends-on-ns interface-ns))))))

(defn import [[statement-type & statement-body] suffixed-top-ns interface-ns]
  (cond
    (= :require statement-type)
    (flatten
      (concat (map (comp str libspec->lib)
                   (filterv #(required-as? % suffixed-top-ns interface-ns)
                            statement-body))
              (map prefix-list->lib-strs
                   (filter sequential?
                           (remove libspec?
                                   statement-body)))))
    (= :import statement-type)
    (map import-list->package-str
         statement-body)))

(defn imports [ns-statements suffixed-top-ns interface-ns]
  (vec (sort (mapcat #(import % suffixed-top-ns interface-ns)
                     (filterv import? ns-statements)))))

(defn skip-slash [path]
  (or (str-util/skip-until path "/")
      path))

(defn namespace-name [root-dir path]
  (when path
    (when-let [file-path (-> (subs path (count root-dir))
                             (skip-slash))]
      (-> file-path
          (str-util/skip-suffixes [".clj" ".cljc"])
          (str/replace "/" ".")
          (str/replace "_" "-")))))

(defn ->namespace [source-dir suffixed-top-ns interface-ns file-path]
  (let [content (file/read-first-statement file-path)
        ns-name (namespace-name source-dir file-path)
        imports (imports content suffixed-top-ns interface-ns)]
    {:name ns-name
     :namespace (-> content second str)
     :file-path file-path
     :imports imports}))

(defn source-namespaces-from-disk [source-dir suffixed-top-ns interface-ns]
  (mapv #(->namespace source-dir suffixed-top-ns interface-ns %)
        (-> source-dir
            file/paths-recursively
            common/filter-clojure-paths)))

(defn namespaces-from-disk [src-dirs test-dirs suffixed-top-ns interface-ns]
  (let [src (vec (mapcat #(source-namespaces-from-disk % suffixed-top-ns interface-ns)
                         src-dirs))
        test (vec (mapcat #(source-namespaces-from-disk % suffixed-top-ns interface-ns)
                          test-dirs))]
    (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))
