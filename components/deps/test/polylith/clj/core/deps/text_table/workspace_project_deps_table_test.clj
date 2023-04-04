(ns polylith.clj.core.deps.text-table.workspace-project-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.data.for-test :as for-test]
            [polylith.clj.core.deps.text-table.project-deps-table :as table]))

(deftest table--with-a-mix-of-dependencies
  (is (= (table/table for-test/workspace for-test/development false)
         ["           a     w"
          "           -     o"
          "           o  u  r"
          "           k  t  k"
          "           a  i  e"
          "  brick    y  l  r"
          "  ----------------"
          "  a-okay   .  x  ."
          "  b-fail   .  .  ."
          "  company  .  .  ."
          "  util     .  .  ."
          "  cli      x  t  x"
          "  worker   .  .  ."])))
