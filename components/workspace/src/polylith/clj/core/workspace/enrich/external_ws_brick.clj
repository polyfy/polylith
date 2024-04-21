(ns ^:no-doc polylith.clj.core.workspace.enrich.external-ws-brick
  (:require [clojure.string :as str]))

(defn brick [{:keys [alias name type]}]
  {:brick {:alias alias
           :type type
           :name name}})

(defn brick-lib [[lib-key {:keys [path]}] dir->alias]
  (when path
    (let [[suffixed-path alias] (first (filter #(str/starts-with? path (first %))
                                               dir->alias))
          brick-path (subs path (count suffixed-path))
          [type brick-name] (cond (str/starts-with? brick-path "bases/") [:base (subs brick-path 6)]
                                  (str/starts-with? brick-path "components/") [:component (subs brick-path 11)])]
      (when (and alias type)
        {:lib-key lib-key
         :alias   alias
         :type type
         :name brick-name
         :path path}))))

(defn full-name [{:keys [alias name]}]
  (str alias "/" name))

(defn convert-libs-to-bricks
  "When we read all workspaces from disk, we treat all :local/root as libraries.
   This function adds the :brick key to libraries that refer bricks in other workspaces."
  [lib-deps configs]
  (let [dir->alias (into {} (map (juxt #(str (:dir %) "/") :alias) (-> configs :workspace :workspaces)))
        brick-libs (filter identity
                           (map #(brick-lib % dir->alias)
                                lib-deps))
        base-names (vec (sort (mapv full-name
                                    (filter #(= :base (:type %))
                                            brick-libs))))
        component-names (vec (sort (mapv full-name
                                         (filter #(= :component (:type %))
                                                 brick-libs))))
        ws-bricks (into {} (map (juxt :lib-key brick)
                                brick-libs))
        lib-deps-with-ws-bricks (merge-with merge lib-deps ws-bricks)]
    [base-names
     component-names
     lib-deps-with-ws-bricks]))
