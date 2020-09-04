(ns polylith.clj.core.deps.text-table.brick-ifc-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.deps.text-table.brick-ifc-deps-table :as deps-table]))

(def brick {:interface-deps ["change" "file" "util" "workspace" "workspace-clj"]})

(deftest table--a-brick-with-interface-deps--returns-a-list-with-those-deps
  (is (= ["  uses         "
          "  -------------"
          "  change       "
          "  file         "
          "  util         "
          "  workspace    "
          "  workspace-clj"]
         (deps-table/table brick color/none))))
