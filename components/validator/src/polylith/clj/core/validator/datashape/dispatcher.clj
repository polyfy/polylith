(ns polylith.clj.core.validator.datashape.dispatcher
  (:require [polylith.clj.core.validator.datashape.toolsdeps1 :as toolsdeps1]
            [polylith.clj.core.validator.datashape.toolsdeps2 :as toolsdeps2]))

(defn throw-unknown-type [input-type]
  (throw (Exception. (str "Unknown input-type: " input-type))))

(defn validate-project-dev-config [input-type config]
  (case input-type
    :toolsdeps1 (toolsdeps1/validate-dev-config config)
    :toolsdeps2 (toolsdeps2/validate-project-dev-config config)
    (throw-unknown-type input-type)))

(defn validate-project-deployable-config [input-type config]
  (case input-type
    :toolsdeps1 (toolsdeps1/validate-project-deployable-config config)
    :toolsdeps2 (toolsdeps2/validate-project-deployable-config config)
    (throw-unknown-type input-type)))
