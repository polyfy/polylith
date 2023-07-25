(ns ^:no-doc polylith.clj.core.ws-file.version-converter
  (:require [polylith.clj.core.version.interface :as version]))

(defn convert [{:keys [version] :as workspace}]
  (let [current-version (version/version)
        file-version (version/version version)
        different? (not= current-version version)]
    (cond-> workspace
            different? (assoc :version file-version))))
