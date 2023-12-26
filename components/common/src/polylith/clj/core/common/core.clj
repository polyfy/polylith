(ns ^:no-doc polylith.clj.core.common.core
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.user-config.interface :as user-config]))

(def entity->short {"w" "w"
                    "p" "p"
                    "b" "b"
                    "c" "c"
                    "workspace" "w"
                    "project" "p"
                    "base" "b"
                    "component" "c"})

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

(defn absolute-path
  "entity-root-path will be passed in as e.g. 'components/invoicer' if a brick,
   or 'projects/invocing' if a project, and nil if the development project
   (dev lives at the root, so keep that path as it is)."
  [path entity-root-path]
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

(defn path->filename [path]
  (last (str/split path #"/")))
    
(defn hidden-file? [path]
  (str/starts-with? (path->filename path) "."))

(defn filter-clojure-paths [paths]
  (filterv #(and 
              (or (str/ends-with? % ".clj")
                  (str/ends-with? % ".cljc"))
              ;; E.g. temporary emacs files might give problems
              (not (hidden-file? %)))
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

(defn find-entity-index [entity-name entities]
  (util/find-first-index #(= entity-name (:name %)) entities))

(defn compact? [{:keys [user-input settings]} view]
  (or (:is-compact user-input)
      (contains? (:compact-views settings) view)))

(defn color-mode [{:keys [color-mode]}]
  (or color-mode (user-config/color-mode)))

(defn invalid-workspace? [{:keys [config-error] :as workspace}]
  (or (nil? workspace)
      (boolean config-error)))

(defn calculate-latest-version? [{:keys [is-update is-outdated]}]
  (or is-update is-outdated))

(defn brick-names-to-test
  "Returns the brick names to include for a project when running the tests.
   The dependencies that are calculated per project are used to test runner
   to decide which tests to run, which means that direct and indirect dependencies
   can sometimes be disabled if :include or :exclude is set."
  [settings project-name all-brick-names]
  (let [include (get-in settings [:projects project-name :test :include])
        exclude (get-in settings [:projects project-name :test :exclude])]
    (set/difference (if include
                      (set include)
                      (set all-brick-names))
                    (if exclude
                      (set exclude)
                      #{}))))
