(ns polylith.clj.core.common.core
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.user-config.interfc :as user-config]))

(defn ns-to-path [namespace]
  (-> namespace
      (str/replace "." "/")
      (str/replace "-" "_")))

(defn path-to-ns [namespace]
  (-> namespace
      (str/replace "/" ".")
      (str/replace "_" "-")))

(defn sufix-ns-with-dot [top-namespace]
  "Makes sure the namespace ends with a dot (.)"
  (if (str/ends-with? top-namespace ".")
    top-namespace
    (str top-namespace ".")))

(defn filter-clojure-paths [paths]
  (filterv #(or (str/ends-with? % ".clj")
                (str/ends-with? % ".cljc"))
           paths))

(defn find-brick [name {:keys [components bases]}]
  (let [bricks (concat components bases)]
    (util/find-first #(= name (:name %)) bricks)))

(defn find-component [name components]
  (util/find-first #(= name (:name %)) components))

(defn find-base [name bases]
  (util/find-first #(= name (:name %)) bases))

(defn- =env [{:keys [name alias]} env]
  (or (= env name)
      (= env alias)))

(defn find-environment [name environments]
  (util/find-first #(=env % name) environments))

(defn color-mode [{:keys [color-mode]}]
  (or color-mode (user-config/color-mode)))

(defn workspace-dir [{:keys [cmd ws-dir]}]
  (if (or (nil? ws-dir)
          (= cmd "test"))
    (file/current-dir)
    ws-dir))
