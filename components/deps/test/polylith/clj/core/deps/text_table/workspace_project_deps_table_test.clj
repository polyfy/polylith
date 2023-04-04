(ns polylith.clj.core.deps.text-table.workspace-project-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.data.for-test :as for-test]
            [polylith.clj.core.deps.text-table.project-deps-table :as table]))

(deftest table--with-a-mix-of-dependencies
  (is (= (table/table for-test/workspace for-test/development false)
         ["           a     h  w"
          "           -     e  o"
          "           o  u  l  r"
          "           k  t  p  k"
          "           a  i  e  e"
          "  brick    y  l  r  r"
          "  -------------------"
          "  a-okay   .  x  .  ."
          "  b-fail   .  .  .  ."
          "  company  .  .  .  ."
          "  util     .  .  .  ."
          "  cli      x  t  t  x"
          "  helper   .  .  .  ."
          "  worker   .  .  .  ."])))
