(ns polylith.clj.core.path-finder.paths
  (:require [clojure.set :as set]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.path-finder.sources-on-disk :as sources]))

(defn project-paths [{:keys [paths profile-src-paths profile-test-paths]}]
  (concat (:src paths) (:test paths) profile-src-paths profile-test-paths))

(defn paths [ws-dir projects profile-to-settings]
  (let [project-paths (mapcat project-paths projects)
        profile-paths (mapcat #(-> % second :paths) profile-to-settings)
        paths (set (concat project-paths profile-paths))
        existing-paths (vec (sort (set (filter #(file/exists (str ws-dir "/" %)) paths))))
        missing-paths (vec (sort (set/difference paths existing-paths)))
        on-disk (sources/source-paths ws-dir)]
    {:existing existing-paths
     :missing missing-paths
     :on-disk on-disk}))
