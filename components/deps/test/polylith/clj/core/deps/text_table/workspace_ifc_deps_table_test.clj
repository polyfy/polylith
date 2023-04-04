(ns polylith.clj.core.deps.text-table.workspace-ifc-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.data.for-test :as for-test]
            [polylith.clj.core.deps.text-table.workspace-deps-table :as table]))

(deftest table--brick-dependencies--should-return-a-correct-table
  (is (= (table/table for-test/workspace)
         ["                 c      "
          "           a  b  o     w"
          "           -  -  m     o"
          "           o  f  p  u  r"
          "           k  a  a  t  k"
          "           a  i  n  i  e"
          "  brick    y  l  y  l  r"
          "  ----------------------"
          "  a-okay   .  .  .  x  ."
          "  b-fail   .  .  .  .  ."
          "  company  .  .  .  .  ."
          "  util     .  .  .  .  ."
          "  cli      x  .  .  t  x"
          "  worker   .  .  .  .  ."])))
