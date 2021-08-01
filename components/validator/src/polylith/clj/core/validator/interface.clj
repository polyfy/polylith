(ns polylith.clj.core.validator.interface
  (:require [polylith.clj.core.validator.core :as core]
            [polylith.clj.core.validator.datashape.dispatcher :as dispatch]
            [polylith.clj.core.validator.datashape.toolsdeps2 :as toolsdeps2]
            [polylith.clj.core.validator.message-printer :as message-printer]))

(defn has-errors? [messages]
  (core/has-errors? messages))

(defn print-messages [workspace]
  (message-printer/print-messages workspace))

(defn validate-project-dev-config [ws-type config]
  (dispatch/validate-project-dev-config ws-type config))

(defn validate-brick-config [config]
  (toolsdeps2/validate-brick-config config))

(defn validate-workspace-config [config]
  (toolsdeps2/validate-workspace-config config))

(defn validate-project-deployable-config [ws-type config]
  (dispatch/validate-project-deployable-config ws-type config))

(defn validate-ws [suffixed-top-ns settings version paths interface-names interfaces components bases projects interface-ns-name user-input color-mode]
  (core/validate-ws suffixed-top-ns settings version paths interface-names interfaces components bases projects interface-ns-name user-input color-mode))
