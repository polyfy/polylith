(ns polylith.clj.core.deps.text-table.project-brick-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.data.for-test :as for-test]
            [polylith.clj.core.deps.text-table.project-brick-deps-table :as table]
            [polylith.clj.core.util.interface.color :as color]))

(deftest table--only-source-dependers
  (is (= (table/table for-test/workspace for-test/development for-test/util color/none)
         ["  used by  <  util  >  uses"
          "  -------              ----"
          "  a-okay                   "
          "  cli (t)                  "])))

(deftest table--test-context-dependees
  (is (= (table/table for-test/workspace for-test/development for-test/cli color/none)
         ["  used by  <  cli  >  uses      "
          "  -------             ----------"
          "                      a-okay    "
          "                      util (t)  "
          "                      helper (t)"
          "                      worker    "])))
