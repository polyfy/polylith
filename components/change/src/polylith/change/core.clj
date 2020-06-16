(ns polylith.change.core
  (:require [polylith.change.brick :as brick]
            [polylith.change.environment :as ws]))

(defn changes [environments hash1 hash2]
  "Returns changed components, bases and environments"
   (let [{:keys [components bases]} (brick/changes hash1 hash2)
         environments (ws/changes environments components bases)]
     {:components components
      :bases bases
      :environments environments}))

(defn with-changes [{:keys [environments] :as workspace} hash1 hash2]
  (assoc workspace :changes (changes environments hash1 hash2)))
