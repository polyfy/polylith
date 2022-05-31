(ns polylith.clj.core.workspace-clj.brick-dirs
  (:require [polylith.clj.core.common.interface.config :as config]))

(defn source-dirs [brick-dir paths]
  (into []
        (comp (filter #(not= "resources" %))
              (map #(str brick-dir "/" %)))
        paths))

(defn top-source-dirs [brick-dir top-src-dir paths]
  (mapv #(str % "/" top-src-dir)
        (source-dirs brick-dir paths)))

(defn top-src-dirs [brick-dir top-src-dir config]
  (top-source-dirs brick-dir top-src-dir (config/src-paths config)))

(defn top-test-dirs [brick-dir top-src-dir config]
  (top-source-dirs brick-dir top-src-dir (config/test-paths config)))
