(ns
  ^{:author "Mark Sto"}
  integrant.system.data_source
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [integrant.pg-ops.interface :as pg-ops])
  (:import (com.zaxxer.hikari HikariDataSource)))

(defn -conn-pool
  ^HikariDataSource
  [{:keys [classname
           subprotocol
           subname
           user
           password]
    :as   db-spec}]
  (log/info "DB Spec:" db-spec)
  (let [conn-pool (doto (HikariDataSource.)
                    (.setDriverClassName classname)
                    (.setJdbcUrl (str "jdbc:" subprotocol ":" subname))
                    (.setUsername user)
                    (.setPassword password))]
    ;; NB: Initializing the pool and performing a validation check.
    (.close (.getConnection conn-pool))
    conn-pool))

(defmethod ig/init-key :integrant.system/data-source
  [_ {:keys [config]}]
  (log/info "Initializing JDBC DataSource")
  (let [db-spec (-> (:db+creds config)
                    (assoc :port (get-in config [:postgres :port]))
                    (pg-ops/->db-spec))]
    (-conn-pool db-spec)))

(defmethod ig/halt-key! :integrant.system/data-source
  [_ ^HikariDataSource data-source]
  (log/info "Closing JDBC DataSource")
  (when data-source
    (.close data-source)))
