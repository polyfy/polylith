(ns polylith.clj.core.create.interfc
  (:require [polylith.clj.core.create.environment :as env]
            [polylith.clj.core.create.workspace :as ws]))

(defn create-workspace [current-dir ws-name ws-ns]
  (ws/create current-dir ws-name ws-ns))

(defn create-environment [current-dir workspace env]
  (env/create current-dir workspace env))

(defn print-alias-message [env color-mode]
  (env/print-alias-message env color-mode))
