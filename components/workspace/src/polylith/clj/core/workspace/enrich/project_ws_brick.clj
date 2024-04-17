(ns ^:no-doc polylith.clj.core.workspace.enrich.project-ws-brick
  (:require [clojure.string :as str]))

(defn extract-brick [[k {:keys [path]}] dir->alias]
  (when path
    (let [[suffixed-path alias] (first (filter #(str/starts-with? path (first %))
                                               dir->alias))]
      [k alias suffixed-path])))

(defn lib-brick [[[k alias suffixed-path] [_ lib-deps]]]
  (let [path (subs (:path lib-deps) (count suffixed-path))]
    (cond
      (str/starts-with? path "bases/") {:type :base
                                        :alias alias
                                        :name (subs path 6)
                                        :lib-key k}
      (str/starts-with? path "components/") {:type :component
                                             :alias alias
                                             :name (subs path 11)
                                             :lib-key k})))

(defn brick [{:keys [alias name type]}]
  {:brick {:alias alias
           :type type
           :name name}})

(defn convert-libs-to-bricks
  "When we read all workspaces from disk, we treat all :local/root as libraries.
   This function identifies the bricks that are read from other
   workspaces, and remove them from PROJECT:lib-deps + returns the brick names
   (which include the workspace alias prefix, e.g. s/util)."
  [lib-deps configs]
  (let [dir->alias (into {} (map (juxt #(str (:dir %) "/") :alias) (-> configs :workspace :workspaces)))
        lib-bricks (mapv lib-brick
                         (filter first
                                  (map (juxt #(extract-brick % dir->alias)
                                             identity)
                                       lib-deps)))
        base-names (mapv :name (filter #(= :base (:type %))
                                       lib-bricks))
        component-names (mapv :name (filter #(= :component (:type %))
                                            lib-bricks))
        ws-bricks (into {} (map (juxt :lib-key brick)
                                lib-bricks))
        lib-deps-with-ws-bricks (merge-with merge lib-deps ws-bricks)]
    [base-names
     component-names
     lib-deps-with-ws-bricks]))
