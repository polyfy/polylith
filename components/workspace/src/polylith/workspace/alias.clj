(ns polylith.workspace.alias
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn suggest-name [name]
  (str/join (map first (str/split name #"-"))))

(defn abbreviation [index short-name full-name]
   [full-name
    (str short-name (inc index))])

(defn new-names [[short-name names]]
  (if (> (count names) 1)
    (map-indexed #(abbreviation %1 short-name %2) (sort names))
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

(defn undefined-env [index env]
  [env (str "?" (inc index))])

(defn abbrivated-envs [env-short-names src-names]
  (let [undefined-envs (set/difference (set src-names)
                                       (set (keys env-short-names)))]
    (if (empty? undefined-envs)
      env-short-names
      (merge env-short-names
             (into {} (map-indexed undefined-env undefined-envs))))))

(defn env->alias [{:keys [env-short-names]} environments]
  (let [src-names (map :name (filter (complement :test?) environments))]
    (if (empty? env-short-names)
      (let [src-name->alias (src-name->alias src-names)]
        (into {} (map #(src-test-name % src-name->alias) environments)))
      (abbrivated-envs env-short-names src-names))))

;(def environments [{:name "core", :test? false}
;                   {:name "core-test" :test? true}
;                   {:name "crawford" :test? false}])
;
;(def src-names (map :name (filter (complement :test?) environments)))
;
;(def env-short-names {"core" "co"})
;                        ;"crawford" "cr"})
;
;
;
;
;
;;(def undefined-envs (set/difference (set src-names)
;;                                    (set (keys env-short-names))))
;
;
