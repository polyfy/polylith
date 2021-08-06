(ns polylith.clj.core.deps.text-table.brick-ifc-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.data.usermanager :as ws]
            [polylith.clj.core.deps.text-table.brick-deps-table :as table]))

(deftest table--only-source-dependencies
  (is (= ["  used by         <  schema  >  uses    "
          "  --------------                --------"
          "  schema-fixture                database"
          "  user                                  "
          "  web                                   "]
         (table/table ws/workspace ws/schema))))

(deftest table--test-context-dependers
  (is (= ["  used by         <  schema-fixture  >  uses  "
          "  --------------                        ------"
          "  department (t)                        schema"
          "  user (t)                                    "]
         (table/table ws/workspace ws/schema-fixture))))

(deftest table--test-context-dependees
  (is (= ["  used by  <  department  >  uses              "
          "  -------                    ------------------"
          "  web                        schema-fixture (t)"]
         (table/table ws/workspace ws/department))))
