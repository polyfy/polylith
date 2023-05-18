(ns project.poly.profiles-test
  (:require [clojure.test :refer :all]))

;; todo: move this to poly-workspace-test

;(defn clean-settings [ws]
;  (let [vcs (dissoc (:vcs ws) :branch :stable-since)]
;    (dissoc (assoc ws :vcs vcs)
;            :user-config-filename
;            :user-home)))
;
;(deftest ws-get-settings
;  (let [actual (clean-settings (ws-get "."
;                                       [:settings]
;                                       "ws-dir:examples/profiles"))]
;    (is (= actual
;           {:active-profiles #{"default"}
;            :color-mode "none"
;            :compact-views #{}
;            :default-profile-name "default"
;            :empty-character "."
;            :interface-ns "interface"
;            :m2-dir (str (System/getProperty "user.home") "/.m2")
;            :profile-to-settings  {"default" {:base-names []
;                                              :component-names ["user1"]
;                                              :lib-deps {"clj-commons/fs" {:size 12819
;                                                                           :type "maven"
;                                                                           :version "1.6.310"}}
;                                              :paths ["components/user1/src"
;                                                      "components/user1/test"]
;                                              :project-names []}
;                                   "extra" {:base-names []
;                                            :component-names ["admin"]
;                                            :lib-deps {}
;                                            :paths ["components/admin/src"
;                                                    "components/admin/test"]
;                                            :project-names []}}
;            :projects {"development" {:alias "dev"
;                                      :test {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
;                       "service" {:alias "s"
;                                  :necessary ["user1"]
;                                  :test {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}}
;            :tag-patterns {:release "v[0-9]*"
;                           :stable "stable-*"}
;            :thousand-separator ","
;            :top-namespace "se.example"
;            :vcs {:git-root (System/getProperty "user.dir")
;                  :name "git"
;                  :auto-add true
;                  :is-git-repo true
;                  :polylith {:branch "master"
;                             :repo "https://github.com/polyfy/polylith.git"}}}))))
;
;(deftest profile-info
;  (is (= (run-cmd "examples/profiles"
;                  "info"
;                  ":no-changes")
;         ["  stable since: 1234567                       "
;          "                                              "
;          "  projects: 2   interfaces: 5                 "
;          "  bases:    2   components: 6                 "
;          "                                              "
;          "  active profiles: default                    "
;          "                                              "
;          "  project      alias  source   dev  extra     "
;          "  --------------------------   ----------     "
;          "  service      s       ---     ---   --       "
;          "  development  dev     s--     s--   --       "
;          "                                              "
;          "  interface    brick           s    dev  extra"
;          "  -------------------------   ---   ----------"
;          "  calculator   calculator1    ---   s--   --  "
;          "  database     database1      st-   st-   --  "
;          "  test-helper  test-helper1   -t-   -t-   --  "
;          "  user         admin          ---   ---   st  "
;          "  user         user1          st-   st-   --  "
;          "  util         util1          st-   st-   --  "
;          "  -            base1          st-   st-   --  "
;          "  -            base2          st-   st-   --  "
;          ""
;          "  Error 107: Missing components in the service project for these interfaces: calculator"])))
;
;(deftest profile-info-where-test-has-changed
;  (is (= (run-cmd "examples/profiles"
;                  "info"
;                  "changed-files:bases/base2/test/se/example/base2/core_test.clj")
;         ["  stable since: 1234567                       "
;          "                                              "
;          "  projects: 2   interfaces: 5                 "
;          "  bases:    2   components: 6                 "
;          "                                              "
;          "  active profiles: default                    "
;          "                                              "
;          "  project        alias  source   dev  extra   "
;          "  ----------------------------   ----------   "
;          "  service +      s       ---     ---   --     "
;          "  development +  dev     s--     s--   --     "
;          "                                              "
;          "  interface    brick           s    dev  extra"
;          "  -------------------------   ---   ----------"
;          "  calculator   calculator1    ---   s--   --  "
;          "  database     database1      st-   st-   --  "
;          "  test-helper  test-helper1   -t-   -t-   --  "
;          "  user         admin          ---   ---   st  "
;          "  user         user1          st-   st-   --  "
;          "  util         util1          st-   st-   --  "
;          "  -            base1          stx   st-   --  "
;          "  -            base2 *        stx   st-   --  "
;          ""
;          "  Error 107: Missing components in the service project for these interfaces: calculator"])))
;
;(deftest profile-info-where-src-has-changed
;  (is (= (run-cmd "examples/profiles"
;                  "info"
;                  "changed-files:bases/base2/src/se/example/base2/core.clj")
;         ["  stable since: 1234567                       "
;          "                                              "
;          "  projects: 2   interfaces: 5                 "
;          "  bases:    2   components: 6                 "
;          "                                              "
;          "  active profiles: default                    "
;          "                                              "
;          "  project        alias  source   dev  extra   "
;          "  ----------------------------   ----------   "
;          "  service +      s       ---     ---   --     "
;          "  development +  dev     s--     s--   --     "
;          "                                              "
;          "  interface    brick           s    dev  extra"
;          "  -------------------------   ---   ----------"
;          "  calculator   calculator1    ---   s--   --  "
;          "  database     database1      st-   st-   --  "
;          "  test-helper  test-helper1   -t-   -t-   --  "
;          "  user         admin          ---   ---   st  "
;          "  user         user1          st-   st-   --  "
;          "  util         util1          st-   st-   --  "
;          "  -            base1          st-   st-   --  "
;          "  -            base2 *        stx   st-   --  "
;          ""
;          "  Error 107: Missing components in the service project for these interfaces: calculator"])))
;
;(deftest profile-info-loc
;  (is (= (run-cmd "examples/profiles"
;                  "info" ":loc"
;                  ":no-changes")
;         ["  stable since: 1234567                                 "
;          "                                                        "
;          "  projects: 2   interfaces: 5                           "
;          "  bases:    2   components: 6                           "
;          "                                                        "
;          "  active profiles: default                              "
;          "                                                        "
;          "  project      alias  source   dev  extra   loc  (t)    "
;          "  --------------------------   ----------   --------    "
;          "  service      s       ---     ---   --       0    0    "
;          "  development  dev     s--     s--   --       0    0    "
;          "                                              0    0    "
;          "                                                        "
;          "  interface    brick           s    dev  extra   loc (t)"
;          "  -------------------------   ---   ----------   -------"
;          "  calculator   calculator1    ---   s--   --       1   0"
;          "  database     database1      st-   st-   --       3   6"
;          "  test-helper  test-helper1   -t-   -t-   --       3   3"
;          "  user         admin          ---   ---   st       3   6"
;          "  user         user1          st-   st-   --       3   5"
;          "  util         util1          st-   st-   --       1   6"
;          "  -            base1          st-   st-   --       1   7"
;          "  -            base2          st-   st-   --       1   5"
;          "                              12    13            16  38"
;          ""
;          "  Error 107: Missing components in the service project for these interfaces: calculator"])))
;
;(deftest profile-info-skip-dev
;  (is (= (run-cmd "examples/profiles"
;                  "info" "skip:dev"
;                  ":no-changes")
;         ["  stable since: 1234567          "
;          "                                 "
;          "  projects: 1   interfaces: 5    "
;          "  bases:    2   components: 6    "
;          "                                 "
;          "  active profiles: default       "
;          "                                 "
;          "  project  alias  source         "
;          "  ----------------------         "
;          "  service  s       ---           "
;          "                                 "
;          "  interface    brick           s "
;          "  -------------------------   ---"
;          "  calculator   calculator1    ---"
;          "  database     database1      st-"
;          "  test-helper  test-helper1   -t-"
;          "  user         admin          ---"
;          "  user         user1          st-"
;          "  util         util1          st-"
;          "  -            base1          st-"
;          "  -            base2          st-"
;          ""
;          "  Error 107: Missing components in the service project for these interfaces: calculator"])))
;
;(deftest profile-deps
;  (is (= (run-cmd "examples/profiles"
;                  "deps")
;         ["                      t      "
;          "                c     e      "
;          "                a     s      "
;          "                l  d  t      "
;          "                c  a  -      "
;          "                u  t  h      "
;          "                l  a  e     b"
;          "                a  b  l  u  a"
;          "                t  a  p  t  s"
;          "                o  s  e  i  e"
;          "  brick         r  e  r  l  2"
;          "  ---------------------------"
;          "  admin         .  .  .  .  ."
;          "  calculator1   .  .  .  .  ."
;          "  database1     x  .  .  x  ."
;          "  test-helper1  .  .  .  .  ."
;          "  user1         .  t  t  .  ."
;          "  util1         .  .  .  .  ."
;          "  base1         .  .  .  .  t"
;          "  base2         .  .  .  .  ."])))
;
;(deftest profile-deps-project
;  (is (= (run-cmd "examples/profiles"
;                  "deps" "project:s")
;         ["                      t      "
;          "                      e      "
;          "                c     s      "
;          "                a  d  t      "
;          "                l  a  -      "
;          "                c  t  h      "
;          "                u  a  e      "
;          "                l  b  l  u  b"
;          "                a  a  p  t  a"
;          "                t  s  e  i  s"
;          "                o  e  r  l  e"
;          "  brick         r  1  1  1  2"
;          "  ---------------------------"
;          "  database1     x  .  .  x  ."
;          "  test-helper1  .  .  .  .  ."
;          "  user1         .  t  t  .  ."
;          "  util1         .  .  .  .  ."
;          "  base1         .  .  .  .  t"
;          "  base2         .  .  .  .  ."])))
;
;(deftest profile-deps-project-src
;  (is (= (run-cmd "examples/profiles"
;                  "deps" "project:s" ":src")
;         ["                  c   "
;          "                  a   "
;          "                  l   "
;          "                  c  u"
;          "                  u  t"
;          "                  l  i"
;          "                  a  l"
;          "                  t  1"
;          "                  o  :"
;          "  brick           r  s"
;          "  --------------------"
;          "  database1:s     x  x"
;          "  test-helper1:s  .  ."
;          "  user1:s         .  ."
;          "  util1:s         .  ."
;          "  base1:s         .  ."
;          "  base2:s         .  ."])))
;
;
;(deftest profile-deps-project-test
;  (is (= (run-cmd "examples/profiles"
;                  "deps" "project:s" ":test")
;         ["                        t      "
;          "                        e      "
;          "                        s      "
;          "                     d  t      "
;          "                  c  a  -      "
;          "                  a  t  h      "
;          "                  l  a  e      "
;          "                  c  b  l  u  b"
;          "                  u  a  p  t  a"
;          "                  l  s  e  i  s"
;          "                  a  e  r  l  e"
;          "                  t  1  1  1  2"
;          "                  o  :  :  :  :"
;          "  brick           r  s  s  s  t"
;          "  -----------------------------"
;          "  database1:t     x  .  .  t  ."
;          "  test-helper1:t  .  .  .  .  ."
;          "  user1:t         .  t  t  .  ."
;          "  util1:t         .  .  .  .  ."
;          "  base1:t         .  .  .  .  t"
;          "  base2:t         .  .  .  .  ."])))
;
;(deftest profile-deps-project-src-test
;  (is (= (run-cmd "examples/profiles"
;                  "deps" "project:s" ":src" ":test")
;         ["                         t      "
;          "                         e      "
;          "                         s      "
;          "                      d  t      "
;          "                   c  a  -      "
;          "                   a  t  h      "
;          "                   l  a  e      "
;          "                   c  b  l  u  b"
;          "                   u  a  p  t  a"
;          "                   l  s  e  i  s"
;          "                   a  e  r  l  e"
;          "                   t  1  1  1  2"
;          "                   o  :  :  :  :"
;          "  brick            r  s  s  s  t"
;          "  ------------------------------"
;          "  database1:st     x  .  .  x  ."
;          "  test-helper1:st  .  .  .  .  ."
;          "  user1:s          .  .  .  .  ."
;          "  user1:t          .  t  t  .  ."
;          "  util1:st         .  .  .  .  ."
;          "  base1:s          .  .  .  .  ."
;          "  base1:t          .  .  .  .  t"
;          "  base2:st         .  .  .  .  ."])))
;
;(deftest profile-deps-brick
;  (is (= (run-cmd "examples/profiles"
;                  "deps" "brick:database1")
;         ["  interface used by  <  database1  >  uses          "
;          "  -----------------                   --------------"
;          "  user1 (t)                           calculator (s)"
;          "                                      util (s)      "])))
;
;(deftest profile-deps-project-brick
;  (is (= (run-cmd "examples/profiles"
;                  "deps" "project:s" "brick:database1")
;         ["  used by    <  database1 (s,t)  >  uses     "
;          "  ---------                         ---------"
;          "  user1 (t)                         util1 (s)"])))
;
;(deftest profile-libs
;  (is (= (run-cmd "examples/profiles"
;                  "libs")
;         ["                                                                         t"
;          "                                                                         e"
;          "                                                                         s"
;          "                                                                         t"
;          "                                                                         -"
;          "                                                                         h"
;          "                                                                         e"
;          "                                                                         l"
;          "                                                                         p"
;          "                                                                         e"
;          "                                                                         r"
;          "  library              version  type      KB   s   dev  default  extra   1"
;          "  ------------------------------------------   -   -------------------   -"
;          "  clj-commons/fs       1.6.310  maven     12   -    -      x       -     ."
;          "  metosin/malli        0.11.1   maven      0   t    x      -       -     x"
;          "  org.clojure/clojure  1.11.1   maven  4,008   x    x      -       -     ."])))
;
;(deftest profile-libs-skip-dev
;  (is (= (run-cmd "examples/profiles"
;                  "libs" "skip:dev")
;         ["                                                   t"
;          "                                                   e"
;          "                                                   s"
;          "                                                   t"
;          "                                                   -"
;          "                                                   h"
;          "                                                   e"
;          "                                                   l"
;          "                                                   p"
;          "                                                   e"
;          "                                                   r"
;          "  library              version  type      KB   s   1"
;          "  ------------------------------------------   -   -"
;          "  clj-commons/fs       1.6.310  maven     12   -   ."
;          "  metosin/malli        0.11.1   maven      0   t   x"
;          "  org.clojure/clojure  1.11.1   maven  4,008   x   ."])))
