(ns poly-rcf.sqldb.interface-test
  (:require [clojure.set :as set]
            [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [poly-rcf.sqldb.interface :as sqldb]))

;; This fails because of https://github.com/polyfy/polylith/issues/261
(deftest new-component-test
  "Test component works by starting a system and running a query."
  (let [config {:dre {:test {:db-spec {:maximum-pool-size 10
                                       :connection-timeout 30000
                                       :idle-timeout 600000
                                       :validation-timeout 5000
                                       :max-lifetime 1800000
                                       :jdbcUrl "jdbc:h2:mem:semmed"}}}}
        sys (component/system-map
             :sqldb (sqldb/new-component config :test))
        s (component/start-system sys)
        sqldb (:sqldb s)
        data-source (:hikari-datasource sqldb)
        _ (log/info "datasource" sqldb)
        ;; read table metadata
        d (with-open [con (jdbc/get-connection data-source)]
            (-> (.getMetaData con) ; produces java.sql.DatabaseMetaData
                (.getTables nil nil nil (into-array ["TABLE" "VIEW"]))
                (rs/datafiable-result-set con)))
        some-tables #{"CONSTANTS" "ENUM_VALUES" "INDEXES"
                      "INDEX_COLUMNS" "INFORMATION_SCHEMA_CATALOG_NAME"}
        all-tables (into #{} (map :TABLE_NAME d))]
              ;; test that some tables are present in metadata
    (is (true? (set/subset? some-tables all-tables)))
    (component/stop-system s)))
