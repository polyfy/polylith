(ns polylith.clj.core.deps.text-table.lib-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.lib-table :as lib-table]))

(def workspace {:settings {:color-mode "none"
                           :empty-char "·"
                           :profile->settings {"default" {:lib-deps {}}
                                               "admin" {:lib-deps {"zprint" {:mvn/version "0.4.15"}}}}
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
                         :lib-dep-names []}]

                :environments [{:alias "core"
                                :lib-deps {"org.clojure/clojure" {:mvn/version "1.10.2"}
                                           "org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}}}
                               {:alias "inv"
                                :lib-deps {"org.clojure/clojure" {:mvn/version "1.10.1"}
                                           "org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}}}
                               {:alias "dev"
                                :lib-deps {"org.clojure/clojure" {:mvn/version "1.10.1"}
                                           "org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}}}]})

(deftest table--components-with-library-dependencies--returns-correct-table
  (is (= ["                                                                                                                                  w   "
          "                                                                                                                                  o   "
          "                                                                                                    p     t  t     u              r   "
          "                                                                                                    a     e  e  t  s  u           k   "
          "                                                                                                    t     s  s  e  e  s     v  w  s   "
          "                                                                                                    h     t  t  x  r  e     a  o  p   "
          "                                                                               c     c              -     -  -  t  -  r     l  r  a   "
          "                                                                            c  o  c  r              f     h  r  -  c  -     i  k  c   "
          "                                                                            h  m  o  e              i  s  e  u  t  o  i     d  s  e   "
          "                                                                            a  m  m  a  d  f     h  n  h  l  n  a  n  n  u  a  p  -   "
          "                                                                            n  a  m  t  e  i  g  e  d  e  p  n  b  f  p  t  t  a  c  c"
          "                                                                            g  n  o  o  p  l  i  l  e  l  e  e  l  i  u  i  o  c  l  l"
          "  library                       version   core  inv   dev  default  admin   e  d  n  r  s  e  t  p  r  l  r  r  e  g  t  l  r  e  j  i"
          "  -------------------------------------   ---------   -------------------   ----------------------------------------------------------"
          "  org.clojure/clojure           1.10.1     -     x     x      -       -     x  x  x  ·  x  x  x  x  x  x  x  x  x  ·  x  x  x  x  x  ·"
          "  org.clojure/clojure           1.10.2     x     -     -      -       -     x  x  x  ·  x  x  x  x  x  x  x  x  x  ·  x  x  x  x  x  ·"
          "  org.clojure/tools.deps.alpha  0.8.695    x     x     x      -       -     ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  x  ·  ·  ·  ·  ·  ·  x  ·"
          "  zprint                        0.4.15     -     -     -      -       x     ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  ·"]
         (lib-table/table workspace))))
