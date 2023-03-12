(ns polylith.clj.core.command.ws-check
  (:require [polylith.clj.core.config-reader.interface :as config-reader]))

(defn valid-ws-deps1-file-found? [ws-dir]
  (let [{:keys [config]} (config-reader/read-project-dev-config-file ws-dir :toolsdeps1)]
    (:polylith config)))

(defn valid-ws-file-found? [ws-dir]
  (let [{:keys [_ error]} (config-reader/read-workspace-config-file ws-dir)]
    (not error)))

(defn valid-workspace-file-found? [ws-dir]
  (or (valid-ws-file-found? ws-dir)
      (valid-ws-deps1-file-found? ws-dir)))
