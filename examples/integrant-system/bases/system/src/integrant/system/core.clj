(ns
  ^{:author "Mark Sto"}
  integrant.system.core
  (:require [clojure.pprint :as pp]
            [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [integrant.system.state :as state]))

;; Integrant System

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
