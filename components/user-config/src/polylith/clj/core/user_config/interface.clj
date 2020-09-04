(ns polylith.clj.core.user-config.interface
  (:require [polylith.clj.core.user-config.core :as core]))

(defn home-dir []
  (core/home-dir))

(defn thousand-separator []
  (core/thousand-separator))

(defn color-mode []
  (core/color-mode))

(defn empty-character []
  (core/empty-character))
