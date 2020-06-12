(ns polylith.git.interface
  (:require [polylith.git.core :as core]))

(defn diff [ws-path hash1 hash2]
  (core/diff ws-path hash1 hash2))
