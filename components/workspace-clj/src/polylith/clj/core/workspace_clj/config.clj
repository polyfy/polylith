(ns polylith.clj.core.workspace-clj.config
  (:require [polylith.clj.core.validator.interface :as validator]))

(defn ws-config-from-disk [ws-dir]
  (let [config (read-string (slurp (str ws-dir "/workspace.edn")))
        message (validator/validate-workspace-config config)]
    (if message
      [config
       {:error (str "Error in ./workspace.edn: " message)}]
      [(assoc config :ws-type :toolsdeps2)])))

(defn with-alias [[project alias]]
  [project {:alias alias}])

;; todo: check that top-namespace is set in config
(defn ws-config-from-dev [{:keys [project-to-alias] :as config}]
  (let [projects (into {} (map with-alias project-to-alias))]
    [(-> config
         (dissoc :project-to-alias)
         (assoc :ws-type :toolsdeps1
                :projects projects))]))
