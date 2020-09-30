(ns polylith.clj.core.common.ws-dir
  (:require [clojure.string :as str]
            [polylith.clj.core.common.core :as core]
            [polylith.clj.core.file.interface :as file])
  (:import (java.io File)))

(defn find-ws-root-dir [path color-mode]
  (if (and (contains? (set (file/files path))
                      "deps.edn")
           (core/valid-config-file? path color-mode))
    path
    (let [parts (str/split path (re-pattern File/separator))]
      (when (or (-> parts empty? not))
        (let [new-path (str/join "/" (drop-last parts))]
          (if (= path new-path)
            (println "  Couldn't find a 'deps.edn' workspace file.")
            (recur new-path color-mode)))))))

(defn workspace-dir [{:keys [cmd ws-dir is-search-for-ws-dir]} color-mode]
  (if (or (and (nil? ws-dir)
               (not is-search-for-ws-dir))
          (= cmd "test"))
    (file/current-dir)
    (if is-search-for-ws-dir
      (find-ws-root-dir (file/absolute-path "") color-mode)
      ws-dir)))
