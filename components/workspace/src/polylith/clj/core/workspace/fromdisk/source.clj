(ns ^:no-doc polylith.clj.core.workspace.fromdisk.source
  (:require [clojure.string :as str]))

(defn suffix [{:keys [file-path]}]
  (last (str/split file-path #"\.")))

(defn source-types
  "Takes a :namespaces structure, and returns the different
   file extensions for the file paths."
  [{:keys [src test]}]
  (set (concat (mapv suffix src)
               (mapv suffix test))))
