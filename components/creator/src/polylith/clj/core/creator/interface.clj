(ns polylith.clj.core.creator.interface
  (:require [polylith.clj.core.creator.base :as b]
            [polylith.clj.core.creator.component :as c]
            [polylith.clj.core.creator.workspace :as ws]
            [polylith.clj.core.creator.environment :as env]))

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
