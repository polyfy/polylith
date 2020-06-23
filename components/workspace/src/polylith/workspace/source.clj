(ns polylith.workspace.source
  (:require [clojure.string :as str]
            [polylith.workspace.environment :as env]))

(defn full-path [ws-path path]
  (let [slash (if (str/starts-with? path "/") "" "/")]
    (str ws-path slash path)))

(defn paths [{:keys [ws-path environments]} env-group include-tests?]
  (let [envs (env/select environments env-group include-tests?)]
    (vec (sort (map #(full-path ws-path %)
                    (mapcat :paths envs))))))

(defn test-namespaces [{:keys [environments bases components]} env-group]
  (let [envs (env/select environments env-group true)
        component-set (into #{} (mapcat :component-names envs))
        env-components (filter #(contains? component-set (:name %))
                               components)
        base-set (into #{} (mapcat :base-names envs))
        env-bases (filter #(contains? base-set (:name %))
                          bases)
        env-bricks (concat env-components env-bases)
        env-test-namespaces (mapcat :namespaces-test env-bricks)
        filtered-test-namespaces (filterv #(str/ends-with? (:name %) "-test") env-test-namespaces)]
    (mapv :namespace filtered-test-namespaces)))
