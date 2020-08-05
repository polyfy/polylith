(ns polylith.clj.core.create.interfc
  (:require [polylith.clj.core.create.environment :as env]
            [polylith.clj.core.create.workspace :as ws]
            [polylith.clj.core.create.component :as c]
            [polylith.clj.core.create.base :as b]))

(defn create-workspace [root-dir ws-name ws-ns]
  (ws/create root-dir ws-name ws-ns))

(defn create-environment [ws-dir workspace env]
  (env/create ws-dir workspace env))

(defn create-component [ws-dir workspace component-name interface-name]
  (c/create ws-dir workspace component-name interface-name))

(defn create-base [ws-dir workspace base-name]
  (b/create ws-dir workspace base-name))

(defn print-alias-message [env color-mode]
  (env/print-alias-message env color-mode))
