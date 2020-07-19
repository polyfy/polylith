(ns polylith.clj.core.deps.text-table.workspace-ifc-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.workspace-ifc-deps-table :as ws-ifc-table]
            [polylith.clj.core.util.interfc.color :as color]
            [clojure.string :as str]))

(def components [{:name "change",
                  :interface-deps ["git" "util"]}
                 {:name "command",
                  :interface-deps ["common" "deps" "help" "test-runner" "util" "workspace"]}
                 {:name "common",
                  :interface-deps ["util"]}
                 {:name "deps",
                  :type "component",
                  :interface-deps ["common" "text-table" "util"]}
                 {:name "file",
                  :interface-deps []}
                 {:name "git",
                  :interface-deps ["shell"]}
                 {:name "help",
                  :interface-deps ["util"]}
                 {:name "shell",
                  :interface-deps []}
                 {:name "test-runner",
                  :interface-deps ["common" "util"]}
                 {:name "text-table",
                  :interface-deps ["util"]}
                 {:name "user-config",
                  :interface-deps []}
                 {:name "util",
                  :interface-deps []}
                 {:name "validate",
                  :interface-deps ["common" "deps" "util"]}
                 {:name "workspace",
                  :interface-deps ["common" "deps" "file" "text-table" "user-config" "util" "validate"]}
                 {:name "workspace-clj",
                  :interface-deps ["common" "file" "util"]}])

(def ws-bases [{:name "cli",
                :interface-deps ["change" "command" "file" "workspace" "workspace-clj"]}
               {:name "z-jocke",
                :interface-deps ["change" "file" "util" "workspace" "workspace-clj"]}])

(deftest table--brick-dependencies--should-return-a-correct-table
  (is (= ["                                                          w"
          "                                                          o"
          "                                        t     u           r"
          "                                        e  t  s           k"
          "                                        s  e  e        w  s"
          "                                        t  x  r     v  o  p"
          "                   c                    -  t  -     a  r  a"
          "                c  o  c                 r  -  c     l  k  c"
          "                h  m  o              s  u  t  o     i  s  e"
          "                a  m  m  d  f     h  h  n  a  n  u  d  p  -"
          "                n  a  m  e  i  g  e  e  n  b  f  t  a  a  c"
          "                g  n  o  p  l  i  l  l  e  l  i  i  t  c  l"
          " brick          e  d  n  s  e  t  p  l  r  e  g  l  e  e  j"
          " ----------------------------------------------------------"
          " change         ·  ·  ·  ·  ·  x  ·  ·  ·  ·  ·  x  ·  ·  ·"
          " command        ·  ·  x  x  ·  ·  x  ·  x  ·  ·  x  ·  x  ·"
          " common         ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·"
          " deps           ·  ·  x  ·  ·  ·  ·  ·  ·  x  ·  x  ·  ·  ·"
          " file           ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·"
          " git            ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·  ·  ·  ·  ·"
          " help           ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·"
          " shell          ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·"
          " test-runner    ·  ·  x  ·  ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·"
          " text-table     ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·"
          " user-config    ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·"
          " util           ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·"
          " validate       ·  ·  x  x  ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·"
          " workspace      ·  ·  x  x  x  ·  ·  ·  ·  x  x  x  x  ·  ·"
          " workspace-clj  ·  ·  x  ·  x  ·  ·  ·  ·  ·  ·  x  ·  ·  ·"
          " cli            x  x  ·  ·  x  ·  ·  ·  ·  ·  ·  ·  ·  x  x"
          " z-jocke        x  ·  ·  ·  x  ·  ·  ·  ·  ·  ·  x  ·  x  x"]
         (str/split-lines
           (ws-ifc-table/table components ws-bases color/none)))))
