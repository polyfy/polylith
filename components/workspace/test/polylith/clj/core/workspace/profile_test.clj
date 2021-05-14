(ns polylith.clj.core.workspace.profile-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interface :as helper]
            [polylith.clj.core.workspace.profile-ws :as profile-ws]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest info
  (is (= (str "  stable since: 1234567\n"
              "\n"
              "  projects: 2   interfaces: 4\n"
              "  bases:    0   components: 5\n"
              "\n"
              "  active profiles: default\n"
              "\n"
              "  project      alias  status   dev  extra   \n"
              "  --------------------------   ----------   \n"
              "  service *    s       ---     ---   --     \n"
              "  development  dev     x--     x--   --     \n"
              "\n"
              "  interface    brick             s    dev  extra\n"
              "  ---------------------------   ---   ----------\n"
              "  database     database1 *      xxx   xx-   --  \n"
              "  test-helper  test-helper1 *   -xx   -x-   --  \n"
              "  user         admin *          ---   ---   xx  \n"
              "  user         user1 *          xxx   xx-   --  \n"
              "  util         util1 *          xxx   xx-   --  \n")
         (profile-ws/execute-command "info"))))

(deftest info-loc
  (is (= (str "  stable since: 1234567\n"
              "\n"
              "  projects: 2   interfaces: 4\n"
              "  bases:    0   components: 5\n"
              "\n"
              "  active profiles: default\n"
              "\n"
              "  project      alias  status   dev  extra   loc  (t)\n"
              "  --------------------------   ----------   --------\n"
              "  service *    s       ---     ---   --       0    0\n"
              "  development  dev     x--     x--   --       0    0\n"
              "                                              0    0\n"
              "\n"
              "  interface    brick             s    dev  extra   loc (t)\n"
              "  ---------------------------   ---   ----------   -------\n"
              "  database     database1 *      xxx   xx-   --       2   6\n"
              "  test-helper  test-helper1 *   -xx   -x-   --       3   3\n"
              "  user         admin *          ---   ---   xx       3   6\n"
              "  user         user1 *          xxx   xx-   --       3   5\n"
              "  util         util1 *          xxx   xx-   --       1   6\n"
              "                                 9     9            12  26\n")
         (profile-ws/execute-command "info" ":loc"))))

(deftest info-no-active-profiles
  (is (= (str "  stable since: 1234567\n"
              "\n"
              "  projects: 2   interfaces: 4\n"
              "  bases:    0   components: 5\n"
              "\n"
              "  project      alias  status   dev  default  extra   \n"
              "  --------------------------   -------------------   \n"
              "  service *    s       ---     ---    --      --     \n"
              "  development  dev     x--     x--    --      --     \n"
              "\n"
              "  interface    brick             s    dev  default  extra"
              "\n"
              "  ---------------------------   ---   -------------------\n"
              "  database     database1 *      xxx   xx-    --      --  \n"
              "  test-helper  test-helper1 *   -xx   -x-    --      --  \n"
              "  user         admin *          ---   ---    --      xx  \n"
              "  user         user1 *          xxx   ---    xx      --  \n"
              "  util         util1 *          xxx   xx-    --      --  \n")
         (profile-ws/execute-command "info" "+"))))

(deftest deps
  (is (= (str "                   t      \n"
              "                   e      \n"
              "                   s      \n"
              "                d  t      \n"
              "                a  -      \n"
              "                t  h      \n"
              "                a  e      \n"
              "                b  l  u  u\n"
              "                a  p  s  t\n"
              "                s  e  e  i\n"
              "  brick         e  r  r  l\n"
              "  ------------------------\n"
              "  admin         .  .  .  .\n"
              "  database1     .  .  .  x\n"
              "  test-helper1  .  .  .  .\n"
              "  user1         t  t  .  .\n"
              "  util1         .  .  .  .\n")
         (profile-ws/execute-command "deps"))))

(deftest deps-project
  (is (= (str "                   t   \n"
              "                   e   \n"
              "                   s   \n"
              "                d  t   \n"
              "                a  -   \n"
              "                t  h   \n"
              "                a  e   \n"
              "                b  l  u\n"
              "                a  p  t\n"
              "                s  e  i\n"
              "                e  r  l\n"
              "  brick         1  1  1\n"
              "  ---------------------\n"
              "  database1     .  .  x\n"
              "  test-helper1  .  .  .\n"
              "  user1         t  t  -\n"
              "  util1         .  .  .\n")
         (profile-ws/execute-command "deps" "project:s"))))

(deftest deps-brick
  (is (= (str "  used by    <  database1  >  uses\n"
              "  ---------                   ----\n"
              "  user1 (t)                   util\n")
         (profile-ws/execute-command "deps" "brick:database1"))))

(deftest deps-project-brick
  (is (= (str "  used by    <  database1  >  uses \n"
              "  ---------                   -----\n"
              "  user1 (t)                   util1\n")
         (profile-ws/execute-command "deps" "project:s" "brick:database1"))))

(deftest libs
  (is (= (str "                                                                                   t\n"
              "                                                                                   e\n"
              "                                                                                   s\n"
              "                                                                                   t\n"
              "                                                                                   -\n"
              "                                                                                   h\n"
              "                                                                                   e\n"
              "                                                                                   l\n"
              "                                                                                   p\n"
              "                                                                                   e\n"
              "                                                                                   r\n"
              "  library                       version  type      KB   s    dev  default  extra   1\n"
              "  ---------------------------------------------------   --   -------------------   -\n"
              "  clj-time                      0.15.2   maven     23   -x   --      -       -     x\n"
              "  me.raynes/fs                  1.4.6    maven     10   --   --      x       -     .\n"
              "  org.clojure/clojure           1.10.1   maven  3,816   x-   x-      -       -     .\n"
              "  org.clojure/tools.deps.alpha  0.8.695  maven     46   x-   x-      -       -     .\n")
         (profile-ws/execute-command "libs"))))

(deftest ws-settings
  (let [ws (read-string (profile-ws/execute-command "ws" "get:settings" "branch:create-deps-files"))
        vcs (dissoc (:vcs ws) :stable-since)
        expected (dissoc (assoc ws :vcs vcs)
                         :user-config-filename
                         :user-home)]
    (is (= {:active-profiles      #{"default"}
            :color-mode           "none"
            :compact-views        #{}
            :default-profile-name "default"
            :empty-character      "."
            :interface-ns         "interface"
            :m2-dir               "/Users/joakimtengstrand/.m2"
            :profile-to-settings  {"default" {:base-names      []
                                              :component-names ["user1"]
                                              :lib-deps        {"me.raynes/fs" {:size    11209
                                                                                :type    "maven"
                                                                                :version "1.4.6"}}
                                              :paths           ["components/user1/src"
                                                                "components/user1/test"]
                                              :project-names   []}
                                   "extra"   {:base-names      []
                                              :component-names ["admin"]
                                              :lib-deps        {}
                                              :paths           ["components/admin/src"
                                                                "components/admin/test"]
                                              :project-names   []}}
            :projects             {"development" {:alias "dev"}
                                   "service"     {:alias "s"}}
            :release-tag-pattern  "v[0-9]*"
            :stable-tag-pattern   "stable-*"
            :thousand-separator   ","
            :top-namespace        "se.example"
            :vcs                  {:branch        "create-deps-files"
                                   :latest-sha    "3bfa6b0db34e0b5b1dc0a68bdd485afe6f8604a1"
                                   :name          "git"
                                   :polylith-repo "https://github.com/polyfy/polylith.git"}
            :version              "0.2.0-alpha10.issue66.03"
            :ws-schema-version    {:breaking     0
                                   :non-breaking 0}
            :ws-type              :toolsdeps2}
           expected))))
