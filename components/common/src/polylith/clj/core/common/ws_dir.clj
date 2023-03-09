(ns polylith.clj.core.common.ws-dir
  (:require [clojure.string :as str]
            [polylith.clj.core.common.core :as core]
            [polylith.clj.core.common.config.validate :as config]
            [polylith.clj.core.file.interface :as file])
  (:import (java.io File)))

(defn find-root-dir [path config-filename valid-fn]
  (if (contains? (set (file/files path))
                 config-filename)
    path
    (let [parts (str/split path (re-pattern File/separator))]
      (when (or (-> parts empty? not))
        (let [new-path (str/join "/" (drop-last parts))]
          (when (not= path new-path)
            (recur new-path config-filename valid-fn)))))))

(defn find-ws-root-dir
  "Start searching for a directory that has a workspace.edn file
   and then continue with an old deps.edn file (containing the :polylith key)
   start by looking in the 'path' and then step one parent directory at a time."
  [path]
  (or
    (find-root-dir path "workspace.edn" config/valid-ws-file-found?)
    (find-root-dir path "deps.edn" config/valid-ws-deps1-file-found?)
    (println "  Couldn't find a valid workspace root config file.")))

(defn workspace-dir [{:keys [ws-dir is-search-for-ws-dir]}]
  (if (and (or (nil? ws-dir)
               (= "." ws-dir))
           (not is-search-for-ws-dir))
    (file/current-dir)
    (if is-search-for-ws-dir
      (find-ws-root-dir (file/absolute-path ""))
      (core/user-path ws-dir))))
