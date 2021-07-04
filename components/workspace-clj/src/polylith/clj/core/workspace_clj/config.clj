(ns polylith.clj.core.workspace-clj.config
  (:require [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.interface :as validator]))

(defn dev-config-from-disk [ws-dir ws-type color-mode]
  (let [config (read-string (slurp (str ws-dir "/deps.edn")))
        message (validator/validate-project-dev-config ws-type config)]
    (if message
      (throw (ex-info (str "  " (color/error color-mode "Error in ./deps.edn: ") message) message))
      config)))

(defn ws-config-from-disk [ws-dir color-mode]
  (let [config (read-string (slurp (str ws-dir "/workspace.edn")))
        message (validator/validate-workspace-config config)]
    (if message
      (throw (ex-info (str "  " (color/error color-mode "Error in ./worspace.edn: ") message) message))
      (assoc config :ws-type :toolsdeps2))))

(defn with-alias [[project alias]]
  [project {:alias alias}])

(defn ws-config-from-dev [{:keys [project-to-alias] :as config}]
  (let [projects (into {} (map with-alias project-to-alias))]
    (-> config
        (dissoc :project-to-alias)
        (assoc :ws-type :toolsdeps1
               :projects projects))))
