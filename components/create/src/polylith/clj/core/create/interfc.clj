(ns polylith.clj.core.create.interfc
  (:require [polylith.clj.core.create.environment :as env]
            [polylith.clj.core.create.workspace :as ws]))

(defn create-workspace [ws-root-path ws-name ws-ns]
  (ws/create ws-root-path ws-name ws-ns))

(defn create-environment [ws-root-path workspace env]
  (env/create ws-root-path workspace env))

(defn print-alias-message [env color-mode]
  (env/print-alias-message env color-mode))
