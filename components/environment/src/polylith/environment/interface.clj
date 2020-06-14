(ns polylith.environment.interface
  (:require [polylith.environment.deps-edn :as deps-edn]))

(defn environments-from-deps-edn [aliases]
  (deps-edn/environments aliases))
