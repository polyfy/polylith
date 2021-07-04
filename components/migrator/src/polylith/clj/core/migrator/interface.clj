(ns polylith.clj.core.migrator.interface
  (:require [polylith.clj.core.migrator.core :as core]))

(defn migrate [ws-dir workspace]
  (core/migrate ws-dir workspace))
