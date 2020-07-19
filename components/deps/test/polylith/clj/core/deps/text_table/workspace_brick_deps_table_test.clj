(ns polylith.clj.core.deps.text-table.workspace-brick-deps-table-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [polylith.clj.core.deps.text-table.workspace-brick-deps-table :as ws-brick-table]
            [polylith.clj.core.util.interfc.color :as color]))

(def environment {:name "core",
                  :deps {"workspace-clj" {:direct ["common" "file" "util"], :indirect []},
                         "test-runner" {:direct ["common" "util"], :indirect []},
                         "command" {:direct ["common" "deps" "help" "util" "workspace"],
                                    :indirect ["file" "text-table" "user-config" "validate"]},
                         "text-table" {:direct ["util"], :indirect []},
                         "util" {:direct [], :indirect []},
                         "validate" {:direct ["common" "deps" "util"], :indirect ["text-table"]},
                         "shell" {:direct [], :indirect []},
                         "workspace" {:direct ["common" "deps" "file" "text-table" "user-config" "util" "validate"], :indirect []},
                         "cli" {:direct ["change" "file" "workspace"],
                                :indirect ["common" "deps" "git" "shell" "text-table" "user-config" "util" "validate"]},
                         "user-config" {:direct [], :indirect []},
                         "git" {:direct ["shell"], :indirect []},
                         "deps" {:direct ["common" "text-table" "util"], :indirect []},
                         "help" {:direct ["util"], :indirect []},
                         "file" {:direct [], :indirect []},
                         "z-jocke" {:direct ["change" "file" "util" "workspace"],
                                    :indirect ["common" "deps" "git" "shell" "text-table" "user-config" "validate"]},
                         "common" {:direct ["util"], :indirect []},
                         "change" {:direct ["git" "util"], :indirect ["shell"]}},})

(def components [{:name "change"}
                 {:name "command",}
                 {:name "common",}
                 {:name "deps",}
                 {:name "file",}
                 {:name "git",}
                 {:name "help",}
                 {:name "shell",}
                 {:name "test-runner",}
                 {:name "text-table",}
                 {:name "user-config",}
                 {:name "util",}
                 {:name "validate",}
                 {:name "workspace",}
                 {:name "workspace-clj",}])

(def ws-bases [{:name "cli",}
               {:name "z-jocke",}])

(deftest table--bricks-with-dependencies--returns-a-correct-matrix
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
          " command        ·  ·  x  x  ·  ·  x  ·  ·  ·  ·  x  ·  x  ·"
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
          " cli            x  ·  ·  ·  x  ·  ·  ·  ·  ·  ·  ·  ·  x  ·"
          " z-jocke        x  ·  ·  ·  x  ·  ·  ·  ·  ·  ·  x  ·  x  ·"]
         (str/split-lines
           (ws-brick-table/table environment components ws-bases color/none)))))
