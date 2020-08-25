(ns polylith.clj.core.migrator.interfc
  (:require [polylith.clj.core.migrator.core :as core]))

(defn migrate [from-dir]
  (core/migrate from-dir))
