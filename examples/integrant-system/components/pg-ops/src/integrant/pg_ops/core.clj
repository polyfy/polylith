(ns
  ^{:author "Mark Sto"}
  integrant.pg-ops.core
  (:require [clojure.java.jdbc :as jdbc]))

(defn ->db-spec
  [{:keys [port dbname user password] :as _db+creds}]
  {:classname   "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname     (format "//localhost:%s/%s" port dbname)
   :user        (or user "postgres")
   :password    (or password "postgres")})

(defn query-version
  [data-source]
  (jdbc/with-db-connection
    [conn {:datasource data-source}]
    (some-> (jdbc/query conn ["SELECT version()"])
            (first)
            :version)))
