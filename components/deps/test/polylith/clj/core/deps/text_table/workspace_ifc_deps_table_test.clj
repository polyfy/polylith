(ns polylith.clj.core.deps.text-table.workspace-ifc-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.data.usermanager :as ws]
            [polylith.clj.core.deps.text-table.workspace-deps-table :as table]))

(deftest table--brick-dependencies--should-return-a-correct-table
  (is (= (table/table ws/workspace2)
         ["                              s      "
          "                              c      "
          "                              h      "
          "                              e      "
          "                        d     m     w"
          "                  a     e     a     e"
          "                  p  d  p     -     b"
          "                  p  a  a     f     -"
          "                  -  t  r  s  i     s"
          "                  s  a  t  c  x     e"
          "                  t  b  m  h  t  u  r"
          "                  a  a  e  e  u  s  v"
          "                  t  s  n  m  r  e  e"
          "  brick           e  e  t  a  e  r  r"
          "  -----------------------------------"
          "  app-state       .  .  .  .  .  .  ."
          "  database        .  .  .  .  .  .  ."
          "  department      .  .  .  .  t  .  ."
          "  schema          .  x  .  .  .  .  ."
          "  schema-fixture  .  .  .  x  .  .  ."
          "  user            .  .  .  x  t  .  ."
          "  web-server      .  .  .  .  .  .  ."
          "  web             x  .  x  x  .  x  x"])))
