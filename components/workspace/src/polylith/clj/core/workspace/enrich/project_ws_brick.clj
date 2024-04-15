(ns ^:no-doc polylith.clj.core.workspace.enrich.project-ws-brick
  (:require [clojure.string :as str]))

(defn extract-brick [[k {:keys [path]}] dir->alias]
  (when path
    (let [[suffixed-path alias] (first (filter #(str/starts-with? path (first %))
                                               dir->alias))]
      [k alias suffixed-path])))

(defn brick-data [[[k alias suffixed-path] [_ lib-deps]]]
  (let [path (subs (:path lib-deps) (count suffixed-path))]
    (cond
      (str/starts-with? path "bases/") [:base (str alias "/" (subs path 6)) k]
      (str/starts-with? path "components/") [:component (str alias "/" (subs path 11)) k]
      :else nil)))

(defn brick [[type name]]
  (identity {:brick {:type type
                     :name name}}))

(defn convert-libs-to-bricks
  "When we read all workspaces from disk, we treat all :local/root as libraries.
   This function identifies the bricks that are read from other
   workspaces, and remove them from PROJECT:lib-deps + returns the brick names
   (which include the workspace alias prefix, e.g. s/util)."
  [lib-deps configs]
  (let [dir->alias (into {} (map (juxt #(str (:dir %) "/") :alias) (-> configs :workspace :workspaces)))
        bricks-data (mapv brick-data
                          (filter first
                                  (map (juxt #(extract-brick % dir->alias)
                                             identity)
                                       lib-deps)))
        base-names (mapv second (filter #(= :base (first %))
                                        bricks-data))
        component-names (mapv second (filter #(= :component (first %))
                                             bricks-data))
        brick-markers (into {} (map (juxt last brick)
                                    bricks-data))
        lib-deps-with-markers (merge-with merge lib-deps brick-markers)]
    [base-names
     component-names
     lib-deps-with-markers]))

(def data {:src {"org.clojure/clojure" {:size    4105111
                                        :type    "maven"
                                        :version "1.11.1"}
                 "shared/util"         {:brick      {:name "s/util"
                                                     :type :component}
                                        :local/root "../../../shared/components/util"
                                        :path       "../shared/components/util"
                                        :size       5068
                                        :type       "local"}}})
(get-in data [:src "shared/util" :brick])