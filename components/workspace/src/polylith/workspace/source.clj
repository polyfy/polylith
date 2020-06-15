(ns polylith.workspace.source
  (:require [clojure.string :as str]
            [polylith.workspace.environment :as env]))

(defn absolute-path [ws-path path]
  (let [slash (if (str/starts-with? path "/") "" "/")]
    (str ws-path slash path)))

(defn paths [{:keys [ws-path environments]} env include-tests?]
  (let [envs (env/select environments env include-tests?)]
    (vec (sort (map #(absolute-path ws-path %)
                    (mapcat :extra-paths envs))))))
