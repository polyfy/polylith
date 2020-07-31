(ns polylith.clj.core.workspace.alias
  (:require [clojure.set :as set]))

(defn src-test-name [{:keys [name]} src-name->short-name]
  [name
   (src-name->short-name name)])

(defn undefined-env [index env]
  [env (str "?" (inc index))])

(defn abbrivated-envs [env-aliases src-names]
  (let [undefined-envs (set/difference (set src-names)
                                       (set (keys env-aliases)))]
    (if (empty? undefined-envs)
      env-aliases
      (merge env-aliases
             (into {} (map-indexed undefined-env undefined-envs))))))

(defn env->alias [{:keys [env-aliases]} environments]
  (let [src-names (mapv :name environments)
        short-names (if env-aliases
                      (if (contains? env-aliases "development")
                        env-aliases
                        (conj env-aliases ["development" "dev"]))
                      {"development" "dev"})]
    (abbrivated-envs short-names src-names)))
