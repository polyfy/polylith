(ns polylith.clj.core.lib.text-table.lib-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interfaces.data :as data]
            [polylith.clj.core.lib.text-table.lib-table :as lib-table]))

(def dev-lib-deps {"antlr" {:local/root "/Users/tengstrand/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar"
                            :type "local"
                            :size 445288
                            :version "2.7.7"}
                   "clj-time/clj-time" {:git/url "https://github.com/clj-time/clj-time.git"
                                        :sha "d9ed4e46c6b42271af69daa1d07a6da2df455fab"
                                        :type "git"
                                        :size 137611
                                        :version "d9ed4e4"}
                   "furkan3ayraktar/polylith-clj-deps-ring" {:git/url "https://github.com/furkan3ayraktar/polylith-clj-deps-ring.git"
                                                             :sha "d74f49da6547fa160f74873d2d0ae3414631750b"
                                                             :deps/root "projects/core"
                                                             :type "git"
                                                             :size 31717
                                                             :version "d74f49d"}
                   "local-clojure" {:local/root "/Users/tengstrand/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar"
                                    :type "local"
                                    :size 445288
                                    :version "2.7.7"}
                   "org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431}
                   "org.clojure/tools.deps"{:version "0.16.1264", :type "maven", :size 47566}})

(def dev-profile-lib-deps {"zprint" {:version "0.4.15", :type "maven", :size 128182}})

(def workspace {:settings {:color-mode "none"
                           :empty-character "."
                           :profile-to-settings {"default" {:paths ["components/user/src"
                                                                    "components/user/resources"
                                                                    "components/user/test"
                                                                    "projects/invoice/test"
                                                                    "projects/core/test"]
                                                            :lib-deps {"zprint" {:version "0.4.15" :type "maven" :size 128182}}
                                                            :component-names ["user"]
                                                            :base-names []
                                                            :project-names ["core" "invoice"]}
                                                 "admin" {:paths ["components/admin/src"
                                                                  "components/admin/resources"
                                                                  "components/admin/test"]
                                                          :lib-deps {"zprint" {:version "0.5.4" :type "maven" :size 0}}
                                                          :component-names ["admin"]
                                                          :base-names []
                                                          :project-names []}}}
                :projects [{:base-names {}
                            :is-dev false
                            :name "core"
                            :alias "core"
                            :lib-deps {:src {"org.clojure/clojure" {:version "1.10.2-alpha1", :type "maven", :size 3908624}
                                             "org.clojure/tools.deps"{:version "0.16.1264", :type "maven", :size 47566}},}
                            :component-names {}}
                           {:base-names {:src ["cli"]}
                            :is-dev false
                            :name "invoice"
                            :alias "inv"
                            :lib-deps {:src {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431}
                                             "org.clojure/tools.deps"{:version "0.16.1264", :type "maven", :size 47566}
                                             "zprint" {:version "0.4.15", :type "maven", :size 128182}}
                                       :test {"zprint" {:version "0.4.15", :type "maven", :size 128182}}}
                            :component-names {:src ["admin" "database" "invoicer" "purchaser"]}}
                           {:base-names {:src ["cli"]}
                            :is-dev true
                            :name "development"
                            :alias "dev"
                            :lib-deps {:src (merge dev-lib-deps dev-profile-lib-deps)
                                       :test (merge dev-lib-deps dev-profile-lib-deps)}
                            :unmerged {:lib-deps {:src dev-lib-deps
                                                  :test {}}}
                            :component-names {:src ["address" "database" "invoicer" "purchaser" "user"]}
                            :profile {:lib-deps dev-profile-lib-deps}}]
                :messages []
                :components [{:lib-deps {:src {"antlr" {:local/root "/Users/tengstrand/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar"
                                                        :type "local"
                                                        :size 445288
                                                        :version "2.7.7"}}}
                              :name "address"
                              :type "component"}
                             {:lib-deps {:src dev-profile-lib-deps
                                         :test dev-profile-lib-deps}
                              :name "admin"
                              :type "component"}
                             {:lib-deps {:src dev-profile-lib-deps}
                              :name "database"
                              :type "component"}
                             {:lib-des {:src {}}
                              :name "invoicer"
                              :type "component"}
                             {:lib-deps {:src {}}
                              :name "purchaser"
                              :type "component"}
                             {:lib-deps {:src dev-profile-lib-deps}
                              :name "user"
                              :type "component"}]
                :bases [{:lib-deps {}
                         :name "cli"
                         :type "base"}]})

(deftest table--show-brick-with-deps--returns-correct-table
  (is (= (lib-table/table workspace false)
         ["                                                                                                                d   "
          "                                                                                                          a     a   "
          "                                                                                                          d     t   "
          "                                                                                                          d  a  a   "
          "                                                                                                          r  d  b  u"
          "                                                                                                          e  m  a  s"
          "                                                                                                          s  i  s  e"
          "  library                                 version        type      KB   core  inv   dev  default  admin   s  n  e  r"
          "  -------------------------------------------------------------------   ---------   -------------------   ----------"
          "  antlr                                   2.7.7          local    434    -     -     x      -       -     x  .  .  ."
          "  clj-time/clj-time                       d9ed4e4        git      134    -     -     x      -       -     .  .  .  ."
          "  furkan3ayraktar/polylith-clj-deps-ring  d74f49d        git       30    -     -     x      -       -     .  .  .  ."
          "  local-clojure                           2.7.7          local    434    -     -     x      -       -     .  .  .  ."
          "  org.clojure/clojure                     1.10.1         maven  3,816    -     x     x      -       -     .  .  .  ."
          "  org.clojure/clojure                     1.10.2-alpha1  maven  3,817    x     -     -      -       -     .  .  .  ."
          "  org.clojure/tools.deps                  0.16.1264      maven     46    x     x     x      -       -     .  .  .  ."
          "  zprint                                  0.4.15         maven    125    -     x     -      x       -     .  x  x  x"
          "  zprint                                  0.5.4          maven      -    -     -     -      -       x     .  .  .  ."])))

(deftest table--show-all-brick-deps--returns-correct-table
  (is (= (lib-table/table workspace false)
         ["                                                                                                                d   "
          "                                                                                                          a     a   "
          "                                                                                                          d     t   "
          "                                                                                                          d  a  a   "
          "                                                                                                          r  d  b  u"
          "                                                                                                          e  m  a  s"
          "                                                                                                          s  i  s  e"
          "  library                                 version        type      KB   core  inv   dev  default  admin   s  n  e  r"
          "  -------------------------------------------------------------------   ---------   -------------------   ----------"
          "  antlr                                   2.7.7          local    434    -     -     x      -       -     x  .  .  ."
          "  clj-time/clj-time                       d9ed4e4        git      134    -     -     x      -       -     .  .  .  ."
          "  furkan3ayraktar/polylith-clj-deps-ring  d74f49d        git       30    -     -     x      -       -     .  .  .  ."
          "  local-clojure                           2.7.7          local    434    -     -     x      -       -     .  .  .  ."
          "  org.clojure/clojure                     1.10.1         maven  3,816    -     x     x      -       -     .  .  .  ."
          "  org.clojure/clojure                     1.10.2-alpha1  maven  3,817    x     -     -      -       -     .  .  .  ."
          "  org.clojure/tools.deps                  0.16.1264      maven     46    x     x     x      -       -     .  .  .  ."
          "  zprint                                  0.4.15         maven    125    -     x     -      x       -     .  x  x  x"
          "  zprint                                  0.5.4          maven      -    -     -     -      -       x     .  .  .  ."])))

(deftest table--show-source-dependencies
  (is (= (lib-table/table data/workspace-01 false)
         ["                                                                              t"
          "                                                                              e"
          "                                                                              s"
          "                                                                              t"
          "                                                                              -"
          "                                                                              h"
          "                                                                              e"
          "                                                                              l"
          "                                                                              p"
          "                                                                              e"
          "  library                 version    type      KB   s   dev  default  extra   r"
          "  -----------------------------------------------   -   -------------------   -"
          "  clj-commons/fs          1.6.310    maven     12   -    -      x       -     ."
          "  clj-time                0.15.2     maven      -   t    -      -       -     t"
          "  org.clojure/clojure     1.10.1     maven  3,816   x    x      -       -     ."
          "  org.clojure/tools.deps  0.16.1264  maven     46   x    x      -       -     ."])))
