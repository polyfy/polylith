(ns polylith.clj.core.deps.text-table.workspace-ifc-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.workspace-ifc-deps-table :as ws-ifc-table]))

(def workspace {:settings {:color-mode "none"
                           :empty-char "·"}
                :interfaces [{:name "change"}
                             {:name "command"}
                             {:name "common"}
                             {:name "deps"}
                             {:name "file"}
                             {:name "git"}
                             {:name "help"}
                             {:name "shell"}
                             {:name "test-runner"}
                             {:name "text-table"}
                             {:name "user-config"}
                             {:name "util"}
                             {:name "validator"}
                             {:name "workspace"}
                             {:name "workspace-clj"}]
                :components [{:name "change",
                              :interface-deps ["git" "util"]}
                             {:name "command",
                              :interface-deps ["common" "deps" "help" "test-runner" "util" "workspace"]}
                             {:name "common",
                              :interface-deps ["util"]}
                             {:name "deps",
                              :type "component",
                              :interface-deps ["common" "text-table" "util"]}
                             {:name "deps2",
                              :type "component",
                              :interface-deps []}
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
                             {:name "validator",
                              :interface-deps ["common" "deps" "util"]}
                             {:name "workspace",
                              :interface-deps ["common" "deps" "file" "text-table" "user-config" "util" "validator"]}
                             {:name "workspace-clj",
                              :interface-deps ["common" "file" "util"]}]
                :bases [{:name "cli",
                         :interface-deps ["change" "command" "file" "workspace" "workspace-clj"]}
                        {:name "z-jocke",
                         :interface-deps ["change" "file" "util" "workspace" "workspace-clj"]}]})

(deftest table--brick-dependencies--should-return-a-correct-table
  (is (= ["                                                           w"
          "                                                           o"
          "                                         t     u           r"
          "                                         e  t  s           k"
          "                                         s  e  e     v  w  s"
          "                                         t  x  r     a  o  p"
          "                    c                    -  t  -     l  r  a"
          "                 c  o  c                 r  -  c     i  k  c"
          "                 h  m  o              s  u  t  o     d  s  e"
          "                 a  m  m  d  f     h  h  n  a  n  u  a  p  -"
          "                 n  a  m  e  i  g  e  e  n  b  f  t  t  a  c"
          "                 g  n  o  p  l  i  l  l  e  l  i  i  o  c  l"
          "  brick          e  d  n  s  e  t  p  l  r  e  g  l  r  e  j"
          "  ----------------------------------------------------------"
          "  change         ·  ·  ·  ·  ·  x  ·  ·  ·  ·  ·  x  ·  ·  ·"
          "  command        ·  ·  x  x  ·  ·  x  ·  x  ·  ·  x  ·  x  ·"
          "  common         ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·"
          "  deps           ·  ·  x  ·  ·  ·  ·  ·  ·  x  ·  x  ·  ·  ·"
          "  deps2          ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·"
          "  file           ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·"
          "  git            ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·  ·  ·  ·  ·"
          "  help           ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·"
          "  shell          ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·"
          "  test-runner    ·  ·  x  ·  ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·"
          "  text-table     ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·"
          "  user-config    ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·"
          "  util           ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·"
          "  validator      ·  ·  x  x  ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·"
          "  workspace      ·  ·  x  x  x  ·  ·  ·  ·  x  x  x  x  ·  ·"
          "  workspace-clj  ·  ·  x  ·  x  ·  ·  ·  ·  ·  ·  x  ·  ·  ·"
          "  cli            x  x  ·  ·  x  ·  ·  ·  ·  ·  ·  ·  ·  x  x"
          "  z-jocke        x  ·  ·  ·  x  ·  ·  ·  ·  ·  ·  x  ·  x  x"]
         (ws-ifc-table/table workspace))))
