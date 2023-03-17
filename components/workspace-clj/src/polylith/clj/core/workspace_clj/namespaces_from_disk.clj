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
  (if (sequential? ns-statements)
    (vec (sort (mapcat #(import % suffixed-top-ns interface-ns)
                       (filterv import? ns-statements))))
    []))

(comment
  (def ns-statements 'x)
  (def suffixed-top-ns "polylith.clj.core.")
  (def interface-ns "interface")
  (imports ns-statements suffixed-top-ns interface-ns)

  (import? ns-statements)

  #__)

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

(defn empty-ns? [content]
  (and (sequential? content)
       (empty? content)))

(defn ns-with-name? [content]
  (and (sequential? content)
       (= (symbol "ns")
          (first content))
       (-> content second boolean)))

(defn ->namespace [ws-dir source-dir suffixed-top-ns interface-ns file-path]
  (let [content (-> file-path file/read-file first)
        ns-name (namespace-name source-dir file-path)
        relative-path (str-util/skip-prefix file-path (str ws-dir "/"))]
    (if (empty-ns? content)
      {:name ns-name
       :namespace ""
       :file-path relative-path
       :imports []}
      (let [imports (imports content suffixed-top-ns interface-ns)
            invalid? (-> content ns-with-name? not)]
        (cond-> {:name ns-name
                 :namespace (if (ns-with-name? content)
                              (-> content second str)
                              "")
                 :file-path relative-path
                 :imports imports}
                invalid? (assoc :invalid true))))))

(comment
  (def source-dir "components/version/src/polylith/clj/core/")
  (def file-path "components/tap/src/polylith/clj/core/tap/core.clj")
  (def source-dir "bases/poly-cli/src/polylith/clj/core/")
  (def file-path "bases/poly-cli/src/polylith/clj/core/poly_cli/core.clj")
  (file/read-file file-path)
  (->namespace source-dir "polylith.clj.core." "interface" file-path)
  #__)

(defn source-namespaces-from-disk [ws-dir source-dir suffixed-top-ns interface-ns]
  (mapv #(->namespace ws-dir source-dir suffixed-top-ns interface-ns %)
        (-> source-dir
            file/paths-recursively
            common/filter-clojure-paths)))

(defn namespaces-from-disk [ws-dir src-dirs test-dirs suffixed-top-ns interface-ns]
  (let [src (vec (mapcat #(source-namespaces-from-disk ws-dir % suffixed-top-ns interface-ns)
                         src-dirs))
        test (vec (mapcat #(source-namespaces-from-disk ws-dir % suffixed-top-ns interface-ns)
                          test-dirs))]
    (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))
