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
  (str/replace (if (symbol? import-list)
                 (->> import-list
                      str
                      (re-find #"(.*)\.\w+$")
                      last)
                 (-> import-list
                     first
                     str))
               "_" "-"))


;; import/require handling

(defn import? [statement]
  (and
    (sequential? statement)
    (contains? #{:import :require} (first statement))))

(defn interface-ns? [ns-name interface-ns]
  (let [interface-nss (common/interface-nss interface-ns)]
    (and
      (-> ns-name nil? not)
      (some #(or (= % ns-name)
                 (str/starts-with? ns-name (str % ".")))
            interface-nss))))

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
    (vec (sort (set (mapcat #(import % suffixed-top-ns interface-ns)
                            (filterv import? ns-statements)))))
    []))

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
  (or (nil? content)
      (and (sequential? content)
           (empty? content))))

(defn ns-with-name? [content]
  (and (sequential? content)
       (= (symbol "ns")
          (first content))
       (-> content second boolean)))

(defn ignore-file? [file-path files-to-ignore]
  (some #(str/ends-with? file-path (str "/" %))
        files-to-ignore))

(defn ->namespace [ws-dir source-dir suffixed-top-ns interface-ns files-to-ignore file-path]
  (let [all-content (file/read-file file-path)
        content (first (drop-while #(-> % ns-with-name? not)
                                   all-content))
        ns-name (namespace-name source-dir file-path)
        relative-path (str-util/skip-prefix file-path (str ws-dir "/"))]
    (if (-> all-content first empty-ns?)
      {:name ns-name
       :namespace ""
       :file-path relative-path
       :imports []}
      (let [imports (imports content suffixed-top-ns interface-ns)
            ignore? (ignore-file? file-path files-to-ignore)
            invalid? (and (not ignore?)
                          (-> content ns-with-name? not))]
        (cond-> {:name ns-name
                 :namespace (if (ns-with-name? content)
                              (-> content second str)
                              "")
                 :file-path relative-path
                 :imports imports}
                ignore? (assoc :is-ignored true)
                invalid? (assoc :is-invalid true))))))

(comment
  (def source-dir "components/version/src/polylith/clj/core/")
  (def file-path "components/tap/src/polylith/clj/core/tap/core.clj")
  (def file-path "components/tap/src/polylith/clj/core/tap/config.clj")
  (def file-path "components/file/src/polylith/clj/core/file/testing.clj")
  (def source-dir "bases/poly-cli/src/polylith/clj/core/")
  (def file-path "bases/poly-cli/src/polylith/clj/core/poly_cli/core.clj")
  (file/read-file file-path)
  (->namespace "." source-dir "polylith.clj.core." "interface" file-path)
  #__)

(defn source-namespaces-from-disk [ws-dir source-dir suffixed-top-ns interface-ns files-to-ignore]
  (mapv #(->namespace ws-dir source-dir suffixed-top-ns interface-ns files-to-ignore %)
        (-> source-dir
            file/paths-recursively
            common/filter-clojure-paths)))

(defn namespaces-from-disk [ws-dir src-dirs test-dirs suffixed-top-ns interface-ns files-to-ignore]
  (let [src (vec (mapcat #(source-namespaces-from-disk ws-dir % suffixed-top-ns interface-ns files-to-ignore)
                         src-dirs))
        test (vec (mapcat #(source-namespaces-from-disk ws-dir % suffixed-top-ns interface-ns files-to-ignore)
                          test-dirs))]
    (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))
