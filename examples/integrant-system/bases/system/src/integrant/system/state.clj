(ns
  ^{:author "Mark Sto"}
  integrant.system.state
  (:require [clojure.tools.logging :as log]
            [clojure.tools.namespace.repl :as repl]
            [integrant.core :as ig]))

(repl/disable-reload!)

(def *state
  "Stores the current Integrant system and its config."
  (atom nil))

(defn start!
  [ig-config]
  (when ig-config
    (log/info "Starting Integrant system")
    (ig/load-namespaces ig-config)
    (reset! *state {:system (ig/init ig-config)
                    :config ig-config})))

(defn stop!
  []
  (log/info "Stopping Integrant system")
  (-> (swap-vals! *state
                  (fn [{:keys [system]}]
                    (when system
                      (ig/halt! system))
                    nil))
      (first)
      :config))

(defn restart!
  []
  (let [ig-config (stop!)]
    (start! ig-config)))
