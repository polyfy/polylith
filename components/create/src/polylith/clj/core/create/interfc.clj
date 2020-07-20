(ns polylith.clj.core.create.interfc
  (:require [polylith.clj.core.create.environment :as env]
            [polylith.clj.core.create.workspace :as ws]))

(defn create-workspace [ws-path ws-name ws-ns]
  (ws/create ws-path ws-name ws-ns))

(defn create-environment [workspace env]
  (env/create workspace env))
