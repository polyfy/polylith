(ns
  ^{:doc    "An embedded PostgreSQL DBMS as a Polylith component"
    :author "Mark Sto"}
  integrant.embedded-pg.interface
  (:require [integrant.embedded-pg.core :as core])
  (:import
    (io.zonky.test.db.postgres.embedded EmbeddedPostgres)))

(defn start-postgres!
  [pg-config]
  (core/start-postgres! pg-config))

(defn stop-postgres!
  [^EmbeddedPostgres embedded-pg]
  (core/stop-postgres! embedded-pg))
