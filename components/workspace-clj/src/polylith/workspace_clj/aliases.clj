(ns polylith.workspace-clj.aliases
  (:require [clojure.string :as str]))

(defn alias->service-or-environment [[k {:keys [extra-paths extra-deps]}] paths deps]
  (let [type (-> k namespace keyword)
        name (name k)
        all-paths (into #{} (concat paths extra-paths))
        all-deps (merge deps extra-deps)
        components (into #{} (map #(hash-map :name (-> %
                                                       (str/replace #"components/" "")
                                                       (str/replace #"/src" "")
                                                       (str/replace #"/resources" ""))
                                             :type "component")

                                  (filter #(str/starts-with? % "components/") all-paths)))
        bases (into #{} (map #(hash-map :name (-> %
                                                  (str/replace #"bases/" "")
                                                  (str/replace #"/src" "")
                                                  (str/replace #"/resources" ""))
                                        :type :base)
                             (filter #(str/starts-with? % "bases/") all-paths)))
        extra-paths (sort (into #{} (filter #(and (not (str/starts-with? % "components/"))
                                                  (not (str/starts-with? % "bases/")))
                                            all-paths)))]
    {:type         type
     :name         name
     :components   (vec (sort-by :name components))
     :bases        (vec (sort-by :name bases))
     :extra-paths  (vec extra-paths)
     :dependencies all-deps}))

(def alias-namespaces #{"service" "env"})

(defn aliases [{:keys [paths deps aliases]}]
  (let [polylith-aliases (filter #(contains? alias-namespaces (-> % key namespace)) aliases)]
    (vec (sort-by (juxt :type :name) (map #(alias->service-or-environment % paths deps) polylith-aliases)))))
