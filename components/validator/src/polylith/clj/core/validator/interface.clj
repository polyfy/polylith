(ns polylith.clj.core.validator.interface
  (:require [polylith.clj.core.validator.core :as core]
            [polylith.clj.core.validator.data :as data]
            [polylith.clj.core.validator.message-printer :as message-printer]))

(defn has-errors? [messages]
  (core/has-errors? messages))

(defn print-messages [workspace]
  (message-printer/print-messages workspace))

(defn validate-deps-edn [config]
  (data/validate-deps-edn config))

(defn validate-ws [suffixed-top-ns settings paths interface-names interfaces components bases environments interface-ns user-input color-mode]
  (core/validate-ws suffixed-top-ns settings paths interface-names interfaces components bases environments interface-ns user-input color-mode))
