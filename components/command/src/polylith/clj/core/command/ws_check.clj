(ns polylith.clj.core.command.ws-check
  (:require [polylith.clj.core.common.interface.config :as config]
            [polylith.clj.core.file.interface :as file]))

(defn valid-ws-deps1-file-found? [path]
  (try
    (and (file/exists (str path "/deps.edn"))
         (:polylith (config/read-deps-file (str path "/deps.edn"))))
    (catch Exception _
      false)))

(defn valid-ws-file-found? [path]
  (try
    (and (file/exists (str path "/workspace.edn"))
         (read-string (slurp (str path "/workspace.edn"))))
    (catch Exception _
      false)))

(defn valid-workspace-file-found? [path]
  (or (valid-ws-file-found? path)
      (valid-ws-deps1-file-found? path)))
