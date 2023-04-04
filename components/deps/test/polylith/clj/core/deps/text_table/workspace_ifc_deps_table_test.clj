(ns polylith.clj.core.deps.text-table.workspace-ifc-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.data.for-test :as for-test]
            [polylith.clj.core.deps.text-table.workspace-deps-table :as table]))

(deftest table--brick-dependencies--should-return-a-correct-table
  (is (= (table/table for-test/workspace)
         ["                 c         "
          "           a  b  o     h  w"
          "           -  -  m     e  o"
          "           o  f  p  u  l  r"
          "           k  a  a  t  p  k"
          "           a  i  n  i  e  e"
          "  brick    y  l  y  l  r  r"
          "  -------------------------"
          "  a-okay   .  .  .  x  .  ."
          "  b-fail   .  .  .  .  .  ."
          "  company  .  .  .  .  .  ."
          "  util     .  .  .  .  .  ."
          "  cli      x  .  .  t  t  x"
          "  helper   .  .  .  .  .  ."
          "  worker   .  .  .  .  .  ."])))
