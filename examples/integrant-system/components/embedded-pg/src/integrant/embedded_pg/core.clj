(ns
  ^{:author "Mark Sto"}
  integrant.embedded-pg.core
  (:require [clojure.java.io :as io])
  (:import
    (io.zonky.test.db.postgres.embedded EmbeddedPostgres EmbeddedPostgres$Builder)
    (java.lang ProcessBuilder$Redirect)))

(defn ->embedded-pg-builder
  ^EmbeddedPostgres$Builder
  [{:keys [port log-file] :as _pg-config}]
  {:pre [(some? port)]}
  (let [pg-builder (-> (EmbeddedPostgres/builder)
                       (.setPort port))]
    (when log-file
      (let [log-redirector (ProcessBuilder$Redirect/appendTo (io/file log-file))]
        (-> pg-builder
            (.setOutputRedirector log-redirector)
            (.setErrorRedirector log-redirector))))
    pg-builder))

(defn start-postgres!
  [pg-config]
  (.start (->embedded-pg-builder pg-config)))

(defn stop-postgres!
  [^EmbeddedPostgres embedded-pg]
  (when embedded-pg
    (.close embedded-pg)))
