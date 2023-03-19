(ns polylith.clj.core.deps.text-table.workspace-project-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.data.usermanager :as ws]
            [polylith.clj.core.deps.text-table.project-deps-table :as table]))

(deftest table--with-a-mix-of-dependencies
  (is (= (table/table ws/workspace ws/usermanager false)
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
          "  schema-fixture  .  +  .  x  .  .  ."
          "  user            .  +  .  x  t  .  ."
          "  web-server      .  .  .  .  .  .  ."
          "  web             x  +  x  x  .  x  x"])))
