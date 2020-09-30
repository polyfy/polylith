(ns polylith.clj.core.workspace.alias
  (:require [clojure.set :as set]))

(defn src-test-name [{:keys [name]} src-name->short-name]
  [name
   (src-name->short-name name)])

(defn undefined-env [index env]
  [env (str "?" (inc index))])

(defn abbreviated-envs [env-to-alias src-names]
  (let [undefined-envs (set/difference (set src-names)
                                       (set (keys env-to-alias)))]
    (if (empty? undefined-envs)
      env-to-alias
      (merge env-to-alias
             (into {} (map-indexed undefined-env undefined-envs))))))

(defn env-to-alias [{:keys [env-to-alias]} environments]
  (let [src-names (mapv :name environments)
        short-names (if env-to-alias
                      (if (contains? env-to-alias "development")
                        env-to-alias
                        (conj env-to-alias ["development" "dev"]))
                      {"development" "dev"})]
    (abbreviated-envs short-names src-names)))
