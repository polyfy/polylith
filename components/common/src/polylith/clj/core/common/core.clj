(ns polylith.clj.core.common.core
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.user-config.interface :as user-config]))

(defn ns-to-path [namespace]
  (-> namespace
      (str/replace "." "/")
      (str/replace "-" "_")))

(defn path-to-ns [namespace]
  (-> namespace
      (str/replace "/" ".")
      (str/replace "_" "-")))

(defn user-path [path]
  (when path
    (if (str/starts-with? path "~/")
      (str (user-config/home-dir)
           (subs path 1))
      path)))

(defn absolute-path [path entity-root-path]
  "entity-root-path will be passed in as e.g. 'components/invoicer' if a brick,
   or 'projects/invocing' if a project, and nil if the development project
   (dev lives at the root, so keep that path as it is)."
  (when path
    (if (or (nil? entity-root-path)
            (str/starts-with? path "/"))
      path
      (if (str/starts-with? path "../../")
        (subs path 6)
        (str entity-root-path "/" path)))))

(defn sufix-ns-with-dot
  "Makes sure the namespace ends with a dot (.)"
  [top-namespace]
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

(defn- =project [{:keys [name alias]} project]
  (or (= project name)
      (= project alias)))

(defn find-project [name projects]
  (util/find-first #(=project % name) projects))

(defn color-mode [{:keys [color-mode]}]
  (or color-mode (user-config/color-mode)))
