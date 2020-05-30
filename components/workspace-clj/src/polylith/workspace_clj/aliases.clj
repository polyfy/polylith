(ns polylith.workspace-clj.aliases
  (:require [clojure.string :as str]))

(defn alias->service-or-environment [[k {:keys [extra-paths extra-deps]}] paths deps]
  (let [type (-> k namespace keyword)
        name (name k)
        all-paths (into #{} (concat paths extra-paths))
        all-deps (merge deps extra-deps)
        components (sort-by :name (into #{} (map #(hash-map :type :component
                                                            :name (-> %
                                                                      (str/replace #"components/" "")
                                                                      (str/replace #"/src" "")
                                                                      (str/replace #"/resources" "")))
                                                 (filter #(str/starts-with? % "components/") all-paths))))
        bases (sort-by :name (into #{} (map #(hash-map :type :base
                                                       :name (-> %
                                                                 (str/replace #"bases/" "")
                                                                 (str/replace #"/src" "")
                                                                 (str/replace #"/resources" "")))
                                            (filter #(str/starts-with? % "bases/") all-paths))))
        extra-paths (sort (into #{} (filter #(and (not (str/starts-with? % "components/"))
                                                  (not (str/starts-with? % "bases/")))
                                            all-paths)))]
    {:type         type
     :name         name
     :components   (vec components)
     :bases        (vec bases)
     :extra-paths  (vec extra-paths)
     :dependencies all-deps}))

(def alias-namespaces #{"service" "env"})

(defn aliases [{:keys [paths deps aliases]}]
  (let [polylith-aliases (filter #(contains? alias-namespaces (-> % key namespace)) aliases)]
    (vec (sort-by (juxt :type :name) (map #(alias->service-or-environment % paths deps) polylith-aliases)))))
