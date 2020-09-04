(ns polylith.clj.core.migrator.interface
  (:require [polylith.clj.core.migrator.core :as core]))

(defn migrate [from-dir]
  (core/migrate from-dir))
