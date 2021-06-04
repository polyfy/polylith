(ns polylith.clj.core.workspace.workspace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.version.interface :as version]
            [polylith.clj.core.test-helper.interface :as helper]
            [polylith.clj.core.workspace.setup-workspace :as profile-ws]))

(use-fixtures :each helper/test-setup-and-tear-down)

(defn clean-get-settings-result [ws-string]
  (let [ws (read-string ws-string)
        vcs (dissoc (:vcs ws) :branch :stable-since)]
    (dissoc (assoc ws :vcs vcs)
            :user-config-filename
            :user-home)))

(def commands [{:cmd "info"
                :args []
                :expected (str "  stable since: 1234567\n"
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
                               "  util         util1 *          xxx   xx-   --  \n")}
               {:cmd "info"
                :args [":loc"]
                :expected (str "  stable since: 1234567\n"
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
                               "                                 9     9            12  26\n")}
               {:cmd "info"
                :args ["+"]
                :expected (str "  stable since: 1234567\n"
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
                               "  util         util1 *          xxx   xx-    --      --  \n")}
               {:cmd "info"
                :args ["skip:dev"]
                :expected (str "  stable since: 1234567\n\n"
                               "  projects: 1   interfaces: 4\n"
                               "  bases:    0   components: 5\n\n"
                               "  active profiles: default\n\n"
                               "  project    alias  status\n"
                               "  ------------------------\n"
                               "  service *  s       ---  \n\n"
                               "  interface    brick             s \n"
                               "  ---------------------------   ---\n"
                               "  database     database1 *      xxx\n"
                               "  test-helper  test-helper1 *   -xx\n"
                               "  user         admin *          ---\n"
                               "  user         user1 *          xxx\n"
                               "  util         util1 *          xxx\n")}
               {:cmd "deps"
                :args []
                :expected (str "                   t      \n"
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
                               "  util1         .  .  .  .\n")}
               {:cmd "deps"
                :args ["project:s"]
                :expected (str "                   t   \n"
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
                               "  util1         .  .  .\n")}
               {:cmd "deps"
                :args ["brick:database1"]
                :expected (str "  used by    <  database1  >  uses\n"
                               "  ---------                   ----\n"
                               "  user1 (t)                   util\n")}
               {:cmd "deps"
                :args ["project:s" "brick:database1"]
                :expected (str "  used by    <  database1  >  uses \n"
                               "  ---------                   -----\n"
                               "  user1 (t)                   util1\n")}
               {:cmd "libs"
                :args []
                :expected (str "                                                                                   t\n"
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
                               "  org.clojure/tools.deps.alpha  0.8.695  maven     46   x-   x-      -       -     .\n")}
               {:cmd "libs"
                :args ["skip:dev"]
                :expected (str "                                                             t\n"
                               "                                                             e\n"
                               "                                                             s\n"
                               "                                                             t\n"
                               "                                                             -\n"
                               "                                                             h\n"
                               "                                                             e\n"
                               "                                                             l\n"
                               "                                                             p\n"
                               "                                                             e\n"
                               "                                                             r\n"
                               "  library                       version  type      KB   s    1\n"
                               "  ---------------------------------------------------   --   -\n"
                               "  clj-time                      0.15.2   maven     23   -x   x\n"
                               "  me.raynes/fs                  1.4.6    maven     10   --   .\n"
                               "  org.clojure/clojure           1.10.1   maven  3,816   x-   .\n"
                               "  org.clojure/tools.deps.alpha  0.8.695  maven     46   x-   .\n")}
               {:cmd "ws"
                :args ["get:settings" "branch:create-deps-files"]
                :clean-result-fn clean-get-settings-result
                :expected {:active-profiles      #{"default"}
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
                           :tag-patterns         {:release "v[0-9]*"
                                                  :stable  "stable-*"}
                           :thousand-separator   ","
                           :top-namespace        "se.example"
                           :vcs                  {:git-root "/Users/joakimtengstrand/source/polylith"
                                                  :name     "git"
                                                  :polylith {:branch     "create-deps-files"
                                                             :latest-sha "3bfa6b0db34e0b5b1dc0a68bdd485afe6f8604a1"
                                                             :repo       "https://github.com/polyfy/polylith.git"}}
                           :version              version/version
                           :ws-schema-version    {:breaking     0
                                                  :non-breaking 0}
                           :ws-type              :toolsdeps2}}])

(deftest run-profile-tests
  (doseq [{:keys [expected actual]} (profile-ws/execute-commands commands)]
    (is (= expected actual))))
