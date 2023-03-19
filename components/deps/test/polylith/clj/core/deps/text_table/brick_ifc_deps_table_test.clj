(ns polylith.clj.core.deps.text-table.brick-ifc-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.data.usermanager :as ws]
            [polylith.clj.core.deps.text-table.brick-deps-table :as table]))

(deftest table--only-source-dependencies
  (is (= (table/table ws/workspace ws/schema)
         ["  used by         <  schema  >  uses    "
          "  --------------                --------"
          "  schema-fixture                database"
          "  user                                  "
          "  web                                   "])))

(deftest table--test-context-dependers
  (is (= (table/table ws/workspace ws/schema-fixture)
         ["  used by         <  schema-fixture  >  uses  "
          "  --------------                        ------"
          "  department (t)                        schema"
          "  user (t)                                    "])))

(deftest table--test-context-dependees
  (is (= (table/table ws/workspace ws/department)
         ["  used by  <  department  >  uses              "
          "  -------                    ------------------"
          "  web                        schema-fixture (t)"])))
