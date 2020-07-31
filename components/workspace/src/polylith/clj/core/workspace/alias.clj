(ns polylith.clj.core.workspace.alias
  (:require [clojure.set :as set]))

(defn src-test-name [{:keys [name]} src-name->short-name]
  [name
   (src-name->short-name name)])

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
  (let [src-names (mapv :name environments)
        short-names (if env-short-names
                      (if (contains? env-short-names "development")
                        env-short-names
                        (conj env-short-names ["development" "dev"]))
                      {"development" "dev"})]
    (abbrivated-envs short-names src-names)))
