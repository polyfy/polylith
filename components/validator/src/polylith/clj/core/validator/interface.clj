(ns polylith.clj.core.validator.interface
  (:require [polylith.clj.core.validator.core :as core]
            [polylith.clj.core.validator.data :as data]
            [polylith.clj.core.validator.message-printer :as message-printer]))

(defn has-errors? [messages]
  (core/has-errors? messages))

(defn print-messages [workspace]
  (message-printer/print-messages workspace))

(defn validate-dev-config [config]
  (data/validate-dev-config config))

(defn validate-deployable-config [config]
  (data/validate-deployable-config config))

(defn validate-ws [suffixed-top-ns settings paths interface-names interfaces components bases projects interface-ns user-input color-mode]
  (core/validate-ws suffixed-top-ns settings paths interface-names interfaces components bases projects interface-ns user-input color-mode))
