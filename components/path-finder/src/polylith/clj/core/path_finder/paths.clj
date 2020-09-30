(ns polylith.clj.core.path-finder.paths
  (:require [clojure.set :as set]
            [polylith.clj.core.file.interface :as file]))

(defn env-paths [{:keys [src-paths test-paths profile-src-paths profile-test-paths]}]
  (concat src-paths test-paths profile-src-paths profile-test-paths))

(defn paths [ws-dir environments profile-to-settings]
  (let [env-paths (mapcat env-paths environments)
        profile-paths (mapcat #(-> % second :paths) profile-to-settings)
        paths (set (concat env-paths profile-paths))
        existing-paths (vec (sort (set (filter #(file/exists (str ws-dir "/"  %)) paths))))
        missing-paths (vec (sort (set/difference paths existing-paths)))]
    {:existing existing-paths
     :missing missing-paths}))
