(ns polylith.workspace.alias
  (:require [clojure.string :as str]))

(defn suggest-name [name]
  (str/join (map first (str/split name #"-"))))

(defn abbreviation [index short-name full-name]
   [full-name
    (str short-name (inc index))])

(defn new-names [[short-name names]]
  (if (> (count names) 1)
    (map-indexed #(abbreviation %1 short-name %2) names)
    [[(first names)
      short-name]]))

(defn src-name->alias [src-names]
  (into {} (mapcat new-names
                   (group-by suggest-name src-names))))

(defn src-test-name [{:keys [name group test?]} src-name->short-name]
  (let [alias (src-name->short-name group)]
    (if test?
      [name (str alias "-test")]
      [name alias])))

(defn env->alias [environments]
  (let [src-names (map :name (filter (complement :test?) environments))
        src-name->alias (src-name->alias src-names)]
    (into {} (map #(src-test-name % src-name->alias) environments))))
