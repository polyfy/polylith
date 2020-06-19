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
