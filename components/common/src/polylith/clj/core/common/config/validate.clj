(ns polylith.clj.core.common.config.validate
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.config.read :as config]))

(defn valid-ws-deps1-file-found? [path]
  (let [filename (str path "/deps.edn")]
    (and (file/exists filename)
         (-> (config/read-deps-file filename) :content :polylith))))

(defn valid-ws-deps2-file-found? [path]
  (let [filename (str path "/workspace.edn")]
    (and (file/exists filename)
         (:content (config/read-deps-file filename)))))

(defn valid-ws-file-found? [path])
