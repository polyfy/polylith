(ns
  ^{:doc    "A set of PostgreSQL-specific operations as a Polylith component"
    :author "Mark Sto"}
  integrant.pg-ops.interface
  (:require [integrant.pg-ops.core :as core]))

(defn ->db-spec
  [db+creds]
  (core/->db-spec db+creds))

(defn query-version
  [data-source]
  (core/query-version data-source))
