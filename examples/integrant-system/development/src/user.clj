(ns
  ^{:author "Mark Sto"}
  user
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [integrant.repl :as ir]
            [integrant.repl.state :as ir.state]

            [integrant.pg-ops.interface :as pg-ops]
            [integrant.system.core :as base-system]))

;; NB: We use the Integrant-REPL library to follow Reloaded Workflow,
;;     which is fine for our use case, since we have a single system.
;;     See https://github.com/weavejester/integrant-repl for details.

(defn start-base-system!
  []
  (let [base-ig-config base-system/default-ig-config]
    (ig/load-namespaces base-ig-config)
    (ir/set-prep! #(ig/expand base-ig-config))
    (ir/go)))

(defn stop-base-system!
  []
  (ir/halt))

(defn system
  []
  ir.state/system)

(comment
  ;; 1. Starting a new system
  ;;    See the logs:
  ;;    - config:326 - Loading configuration
  ;;    - embedded-pg:326 - Initializing Postgres with config: ...
  ;;    - data_source:326 - Initializing JDBC DataSource
  (start-base-system!) ;=> :initiated

  ;; 2. Get the current system in full
  (system) ;=> #:integrant.system{:config ... :embedded-pg ... :data-source ...}

  ;; 3. Get a particular component (key)
  (:integrant.system/config (system)) ;=> {:postgres ... :db+creds ...}

  ;; 4. Check that our code logic works
  ;;    See the logs:
  ;;    - user:326 - PostgreSQL ...
  (log/info (pg-ops/query-version (:integrant.system/data-source (system))))

  ;; 5. Check how a system restart works
  ;;    See the logs:
  ;;    - data_source:326 - Closing JDBC DataSource
  ;;    - embedded-pg:326 - Halting Postgres
  ;;    - config:326 - Loading configuration
  ;;    - embedded-pg:326 - Initializing Postgres with config: ...
  ;;    - data_source:326 - Initializing JDBC DataSource
  (start-base-system!) ;=> :initiated

  ;; 6. Check how to reload source files and restart the system in one go
  ;;    See the logs:
  ;;    - data_source:326 - Closing JDBC DataSource
  ;;    - embedded-pg:326 - Halting Postgres
  ;;    - :reloading (integrant.config.core integrant.config.interface ... user)
  ;;    - config:326 - Loading configuration
  ;;    - embedded-pg:326 - Initializing Postgres with config: ...
  ;;    - data_source:326 - Initializing JDBC DataSource
  (ir/reset) ;=> :resumed

  ;; 7. Stopping the running system
  ;;    See the logs:
  ;;    - data_source:326 - Closing JDBC DataSource
  ;;    - embedded-pg:326 - Halting Postgres
  (stop-base-system!) ;=> :halted

  ;; NB: Since our base starts a new system process (for embedded PostgreSQL)
  ;;     it is recommended to always stop it manually prior to stopping REPL.
  ;;     Otherwise, you will have to kill the system process manually.
  .)
