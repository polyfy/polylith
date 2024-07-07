(ns
  ^{:author "Mark Sto"}
  integrant.system.core
  (:require [clojure.pprint :as pp]
            [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [integrant.system.state :as state]))

;; Integrant System

;; NB: For our system we follow the Integrant conventions on loading namespaces
;;     that contain the system components (a.k.a. keys in Integrant's parlance)
;;     named with qualified keywords that match their namespaces. For instance,
;;     the 'Config' component rests in the `integrant.system.config` namespace,
;;     and therefore reclaims a `:integrant.system/config` key in a system map.
;;
;;     This approach leverages the uniqueness of Polylith base names and allows
;;     us to use the same stateful Polylith component across multiple Integrant
;;     systems/bases without thinking too much about its name.
;;
;;     Another option is to use random keys, e.g. `:app/config`, though it may
;;     lead to possible naming collisions, especially when you plan to somehow
;;     mix and match these keys between different Integrant (sub)systems.

(def default-ig-config
  {:integrant.system/config      {}
   :integrant.system/embedded-pg {:config (ig/ref :integrant.system/config)
                                  #_#_:log-file "path/to/pg-logs-redirection"}
   :integrant.system/data-source {:config   (ig/ref :integrant.system/config)
                                  :postgres (ig/ref :integrant.system/embedded-pg)}})

(defn halt-system! []
  (state/stop!)
  ::stopped)

(defn init-system!
  ([]
   (init-system! default-ig-config))
  ([ig-config]
   (log/info "Starting system with config:\n"
             (with-out-str (pp/pprint ig-config)))
   (try
     (halt-system!)
     (state/start! ig-config)
     ::started
     (catch Exception ex
       (log/error ex "Failed to start the system")
       (halt-system!)
       ::failed-to-start))))

;; Base API

;; NB: Properly shutting production systems down is full of its own shenanigans,
;;     so we don't do much here. For more information, see the article "Killing
;;     me softly: Graceful shutdowns in Clojure".

(defn shutdown!
  []
  (let [halt-res (halt-system!)]
    (shutdown-agents)
    halt-res))

(defn launch!
  []
  (.addShutdownHook (Runtime/getRuntime) (Thread. shutdown!))
  (init-system!))

(defn -main
  [& _]
  (launch!))
