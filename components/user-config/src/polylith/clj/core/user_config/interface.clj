(ns polylith.clj.core.user-config.interface
  (:require [polylith.clj.core.user-config.core :as core]))

(defn home-dir []
  (core/home-dir))

(defn file-path []
  (core/config-file-path))

(defn content []
  (core/config-content))

(defn color-mode []
  (core/color-mode))

(defn empty-character []
  (core/empty-character))

(defn m2-dir []
  (core/m2-dir))

(defn m2-home-dir? [m2-dir]
  (core/m2-home-dir? m2-dir))

(defn thousand-separator []
  (core/thousand-separator))

(defn legacy-config-file-path []
  (core/legacy-config-file-path))
