(ns polylith.clj.core.deps.text-table.brick-ifc-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.data.for-test :as for-test]
            [polylith.clj.core.deps.text-table.brick-deps-table :as table]))

(deftest table--only-source-dependers
  (is (= (table/table for-test/workspace for-test/util)
         ["  used by  <  util  >  uses"
          "  -------              ----"
          "  a-okay                   "
          "  cli (t)                  "])))

(deftest table--test-context-dependees
  (is (= (table/table for-test/workspace for-test/cli)
         ["  used by  <  cli  >  uses      "
          "  -------             ----------"
          "                      a-okay    "
          "                      util (t)  "
          "                      helper (t)"
          "                      worker    "])))
