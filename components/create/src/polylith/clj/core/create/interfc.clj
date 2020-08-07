(ns polylith.clj.core.create.interfc
  (:require [polylith.clj.core.create.environment :as env]
            [polylith.clj.core.create.workspace :as ws]
            [polylith.clj.core.create.component :as c]
            [polylith.clj.core.create.base :as b]))

(defn create-workspace [root-dir ws-name top-ns]
  (ws/create root-dir ws-name top-ns))

(defn create-environment [workspace env]
  (env/create workspace env))

(defn create-component [workspace component-name interface-name]
  (c/create workspace component-name interface-name))

(defn create-base [workspace base-name]
  (b/create workspace base-name))

(defn print-alias-message [env color-mode]
  (env/print-alias-message env color-mode))
