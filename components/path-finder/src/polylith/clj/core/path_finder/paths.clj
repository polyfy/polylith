(ns ^:no-doc polylith.clj.core.path-finder.paths
  (:require [clojure.set :as set]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.path-finder.sources-on-disk :as sources]))

(defn project-paths [{:keys [paths profile-src-paths profile-test-paths]}]
  (concat (:src paths) (:test paths) profile-src-paths profile-test-paths))

(def extract-project-paths-xf (mapcat project-paths))
(def extract-profile-paths-xf (mapcat #(-> % second :paths)))

(defn paths [ws-dir projects profile-to-settings]
  (let [paths (-> #{}
                  (into extract-project-paths-xf projects)
                  (into extract-profile-paths-xf profile-to-settings))
        existing-paths (into (sorted-set) (filter #(file/exists (str ws-dir "/" %))) paths)
        missing-paths (vec (sort (set/difference paths existing-paths)))
        on-disk (sources/source-paths ws-dir)]
    {:existing (vec existing-paths)
     :missing missing-paths
     :on-disk on-disk}))
