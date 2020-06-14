(ns polylith.workspace.source
  (:require [clojure.string :as str]))

(defn absolute-path [ws-path path]
  (let [slash (if (str/starts-with? path "/") "" "/")]
    (str ws-path slash path)))

(defn extra-paths [{:keys [group test? extra-paths]} env include-tests?]
  (when  (and (= group env)
              (or include-tests?
                  (not test?)))
    extra-paths))

(defn paths [{:keys [ws-path environments]} env include-tests?]
  (vec (sort (map #(absolute-path ws-path %)
                  (mapcat #(extra-paths % env include-tests?) environments)))))
