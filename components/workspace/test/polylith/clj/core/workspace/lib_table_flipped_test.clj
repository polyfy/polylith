(ns polylith.clj.core.workspace.lib-table-flipped-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.text-table.lib-table-flipped :as lib-table-flipped]))

(def workspace {:settings {:color-mode "none"
                           :ns->lib {"clojure" "org.clojure/clojure"
                                     "clojure.core.matrix" "net.mikera/core.matrix"
                                     "clojure.tools.deps" "org.clojure/tools.deps.alpha"}}
                :components [{:name "change"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "command"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "common"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "creator"
                              :type "component"
                              :lib-dep-names []}
                             {:name "deps"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "file"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "git"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "help"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "path-finder"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "shell"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "test-helper"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "test-runner"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"
                                              "org.clojure/tools.deps.alpha"]}
                             {:name "text-table"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "user-config"
                              :type "component"
                              :lib-dep-names []}
                             {:name "user-input"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "util"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "validator"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "workspace"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"]}
                             {:name "workspace-clj"
                              :type "component"
                              :lib-dep-names ["org.clojure/clojure"
                                              "org.clojure/tools.deps.alpha"]}]
                :bases [{:name "cli"
                         :type "base"
                         :lib-dep-names []}]})

(deftest table--components-with-librariy-dependencies--returns-correct-table
  (is (= ["                    o"
          "                    r"
          "                    g"
          "                    ."
          "                    c"
          "                    l"
          "                    o"
          "                    j"
          "                    u"
          "                 o  r"
          "                 r  e"
          "                 g  /"
          "                 .  t"
          "                 c  o"
          "                 l  o"
          "                 o  l"
          "                 j  s"
          "                 u  ."
          "                 r  d"
          "                 e  e"
          "                 /  p"
          "                 c  s"
          "                 l  ."
          "                 o  a"
          "                 j  l"
          "                 u  p"
          "                 r  h"
          "  bricks         e  a"
          "  -------------------"
          "  change         x  -"
          "  command        x  -"
          "  common         x  -"
          "  creator        -  -"
          "  deps           x  -"
          "  file           x  -"
          "  git            x  -"
          "  help           x  -"
          "  path-finder    x  -"
          "  shell          x  -"
          "  test-helper    x  -"
          "  test-runner    x  x"
          "  text-table     x  -"
          "  user-config    -  -"
          "  user-input     x  -"
          "  util           x  -"
          "  validator      x  -"
          "  workspace      x  -"
          "  workspace-clj  x  x"
          "  cli            -  -"]
         (lib-table-flipped/table workspace))))
