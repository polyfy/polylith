(ns polylith.clj.core.common.core
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc :as util]))

(defn ns-to-path [namespace]
  (-> namespace
      (str/replace "." "/")
      (str/replace "-" "_")))

(defn path-to-ns [namespace]
  (-> namespace
      (str/replace "/" ".")
      (str/replace "_" "-")))

(defn sufix-ns-with-dot [top-namespace]
  "Makes sure the namespace ends with a dot (.) - if not empty."
  (if (str/blank? top-namespace)
    ""
    (if (str/ends-with? top-namespace ".")
      top-namespace
      (str top-namespace "."))))

(defn filter-clojure-paths [paths]
  (filterv #(or (str/ends-with? % ".clj")
                (str/ends-with? % ".cljc"))
           paths))

(defn find-brick [{:keys [components bases]} brick-name]
  (let [bricks (concat components bases)]
    (util/find-first #(= brick-name (:name %)) bricks)))

(defn find-component [component-name components]
  (util/find-first #(= component-name (:name %)) components))

(defn find-base [base-name bases]
  (util/find-first #(= base-name (:name %)) bases))

(defn- =env [{:keys [name alias]} env]
  (or (= env name)
      (= env alias)))

(defn find-environment [environment-name environments]
  (util/find-first #(=env % environment-name) environments))
