(ns ^:no-doc polylith.clj.core.validator.interface
  (:require [polylith.clj.core.validator.core :as core]
            [polylith.clj.core.validator.datashape.dispatcher :as dispatch]
            [polylith.clj.core.validator.datashape.toolsdeps2 :as toolsdeps2]
            [polylith.clj.core.validator.message-printer :as message-printer]))

(defn has-errors? [messages]
  (core/has-errors? messages))

(defn error-messages [messages]
  (core/error-messages messages))

(defn print-messages [workspace]
  (message-printer/print-messages workspace))

(defn validate-project-dev-config [ws-type config filename]
  (dispatch/validate-project-dev-deps-config ws-type config filename))

(defn validate-brick-config [_ config filename]
  (toolsdeps2/validate-brick-config config filename))

(defn validate-workspace-config [config]
  (toolsdeps2/validate-workspace-config config))

(defn validate-project-deployable-deps-config [ws-type config filename]
  (dispatch/validate-project-deployable-deps-config ws-type config filename))

(defn validate-ws [settings paths interface-names interfaces profiles components bases projects workspaces config-errors interface-ns user-input color-mode]
  (core/validate-ws settings paths interface-names interfaces profiles components bases projects workspaces config-errors interface-ns user-input color-mode))
