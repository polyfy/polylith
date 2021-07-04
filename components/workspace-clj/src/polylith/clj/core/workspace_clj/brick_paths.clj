(ns polylith.clj.core.workspace-clj.brick-paths
  (:require [polylith.clj.core.file.interface :as file]))

(defn existing-paths [component-dir paths]
  (filterv #(file/exists (str component-dir "/" %)) paths))

(defn source-paths [brick-dir {:keys [paths aliases]}]
  {:src  (existing-paths brick-dir paths)
   :test (existing-paths brick-dir (-> aliases :test :extra-paths))})
