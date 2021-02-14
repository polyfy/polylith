(ns polylith.clj.core.validator.interface
  (:require [polylith.clj.core.validator.core :as core]
            [polylith.clj.core.validator.datashape.shared :as shared]
            [polylith.clj.core.validator.datashape.toolsdeps1 :as toolsdeps1]
            [polylith.clj.core.validator.datashape.toolsdeps2 :as toolsdeps2]
            [polylith.clj.core.validator.message-printer :as message-printer]))

(defn has-errors? [messages]
  (core/has-errors? messages))

(defn print-messages [workspace]
  (message-printer/print-messages workspace))

(defn validate-dev-config [input-type config]
  (case input-type
    :toolsdeps1 (toolsdeps1/validate-dev-config config)
    :toolsdeps2 (toolsdeps2/validate-dev-config config)
    (throw (Exception. (str "Unknown input-type: " input-type)))))

(defn validate-brick-config [config]
  (toolsdeps2/validate-brick-config config))

(defn validate-workspace-config [config]
  (toolsdeps2/validate-workspace-config config))

(defn validate-deployable-config [config]
  (shared/validate-deployable-config config))

(defn validate-ws [suffixed-top-ns settings paths interface-names interfaces components bases projects interface-ns user-input color-mode]
  (core/validate-ws suffixed-top-ns settings paths interface-names interfaces components bases projects interface-ns user-input color-mode))
