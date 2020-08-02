(ns polylith.clj.core.create.interfc
  (:require [polylith.clj.core.create.environment :as env]
            [polylith.clj.core.create.workspace :as ws]
            [polylith.clj.core.create.component :as c]
            [polylith.clj.core.create.base :as b]))

(defn create-workspace [current-dir ws-name ws-ns]
  (ws/create current-dir ws-name ws-ns))

(defn create-environment [current-dir workspace env]
  (env/create current-dir workspace env))

(defn create-component [current-dir workspace component]
  (c/create current-dir workspace component))

(defn create-base [current-dir workspace base]
  (b/create current-dir workspace base))

(defn print-alias-message [env color-mode]
  (env/print-alias-message env color-mode))
