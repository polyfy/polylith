(ns polylith.clj.core.workspace-clj.ignore-files-settings
  (:require [clojure.string :as str]))

(defn file-path [namespace]
  (str/replace (str namespace) "-" "_"))

(defn convert-brick [[entity-name {:keys [ignore-files] :as settings}]]
  (let [files-to-ignore (mapv file-path
                              (if (sequential? ignore-files)
                                ignore-files
                                []))]
    [entity-name (cond-> settings
                         (seq files-to-ignore) (assoc :ignore-files files-to-ignore))]))

(defn convert [bricks]
  (when bricks
    (into (sorted-map) (map convert-brick bricks))))
