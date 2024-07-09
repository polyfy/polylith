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

(defn get
  "Retrieves an Integrant system component by its key."
  [component-key]
  (get-in @*state [:system component-key]))

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


;; NB: This fn only exists for the completeness of the example.
;;     Normally you should avoid restarting production systems.
;;     See the `user.clj` on how to restart the system in REPL.
(defn ^:tests-only -restart!
  []
  (let [ig-config (stop!)]
    (start! ig-config)))
