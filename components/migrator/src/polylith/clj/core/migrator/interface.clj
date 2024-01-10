(ns ^:no-doc polylith.clj.core.migrator.interface
  (:require [polylith.clj.core.migrator.migrate :as migrate]))

(defn migrate [ws-dir workspace]
  (migrate/migrate ws-dir workspace))

