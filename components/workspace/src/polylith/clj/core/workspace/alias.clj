(ns polylith.clj.core.workspace.alias
  (:require [clojure.set :as set]))

(defn src-test-name [{:keys [name]} src-name->short-name]
  [name
   (src-name->short-name name)])

(defn undefined-env [index env]
  [env (str "?" (inc index))])

(defn abbreviated-envs [env->alias src-names]
  (let [undefined-envs (set/difference (set src-names)
                                       (set (keys env->alias)))]
    (if (empty? undefined-envs)
      env->alias
      (merge env->alias
             (into {} (map-indexed undefined-env undefined-envs))))))

(defn env->alias [{:keys [env->alias]} environments]
  (let [src-names (mapv :name environments)
        short-names (if env->alias
                      (if (contains? env->alias "development")
                        env->alias
                        (conj env->alias ["development" "dev"]))
                      {"development" "dev"})]
    (abbreviated-envs short-names src-names)))
