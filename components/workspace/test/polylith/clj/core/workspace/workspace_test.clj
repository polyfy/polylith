(ns polylith.clj.core.workspace.workspace-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
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
                :expected ["  stable since: 1234567"
                           ""
                           "  projects: 2   interfaces: 4"
                           "  bases:    0   components: 5"
                           ""
                           "  active profiles: default"
                           ""
                           "  project        alias  status   dev  extra   "
                           "  ----------------------------   ----------   "
                           "  service *      s       ---     ---   --     "
                           "  development +  dev     s--     s--   --     "
                           ""
                           "  interface    brick             s    dev  extra"
                           "  ---------------------------   ---   ----------"
                           "  database     database1 *      stx   st-   --  "
                           "  test-helper  test-helper1 *   -tx   -t-   --  "
                           "  user         admin *          ---   ---   st  "
                           "  user         user1 *          stx   st-   --  "
                           "  util         util1 *          stx   st-   --  "]}
               {:cmd "info"
                :args [":loc"]
                :expected ["  stable since: 1234567"
                           ""
                           "  projects: 2   interfaces: 4"
                           "  bases:    0   components: 5"
                           ""
                           "  active profiles: default"
                           ""
                           "  project        alias  status   dev  extra   loc  (t)"
                           "  ----------------------------   ----------   --------"
                           "  service *      s       ---     ---   --       0    0"
                           "  development +  dev     s--     s--   --       0    0"
                           "                                                0    0"
                           ""
                           "  interface    brick             s    dev  extra   loc (t)"
                           "  ---------------------------   ---   ----------   -------"
                           "  database     database1 *      stx   st-   --       2   6"
                           "  test-helper  test-helper1 *   -tx   -t-   --       3   3"
                           "  user         admin *          ---   ---   st       3   6"
                           "  user         user1 *          stx   st-   --       3   5"
                           "  util         util1 *          stx   st-   --       1   6"
                           "                                 9     9            12  26"]}
               {:cmd "info"
                :args ["+"]
                :expected ["  stable since: 1234567"
                           ""
                           "  projects: 2   interfaces: 4"
                           "  bases:    0   components: 5"
                           ""
                           "  project        alias  status   dev  default  extra   "
                           "  ----------------------------   -------------------   "
                           "  service *      s       ---     ---    --      --     "
                           "  development +  dev     s--     s--    --      --     "
                           ""
                           "  interface    brick             s    dev  default  extra"
                           "  ---------------------------   ---   -------------------"
                           "  database     database1 *      stx   st-    --      --  "
                           "  test-helper  test-helper1 *   -tx   -t-    --      --  "
                           "  user         admin *          ---   ---    --      st  "
                           "  user         user1 *          stx   ---    st      --  "
                           "  util         util1 *          stx   st-    --      --  "]}
               {:cmd "info"
                :args ["skip:dev"]
                :expected ["  stable since: 1234567"
                           ""
                           "  projects: 1   interfaces: 4"
                           "  bases:    0   components: 5"
                           ""
                           "  active profiles: default"
                           ""
                           "  project    alias  status"
                           "  ------------------------"
                           "  service *  s       ---  "
                           ""
                           "  interface    brick             s "
                           "  ---------------------------   ---"
                           "  database     database1 *      stx"
                           "  test-helper  test-helper1 *   -tx"
                           "  user         admin *          ---"
                           "  user         user1 *          stx"
                           "  util         util1 *          stx"]}
               {:cmd "deps"
                :args []
                :expected ["                   t      "
                           "                   e      "
                           "                   s      "
                           "                d  t      "
                           "                a  -      "
                           "                t  h      "
                           "                a  e      "
                           "                b  l  u  u"
                           "                a  p  s  t"
                           "                s  e  e  i"
                           "  brick         e  r  r  l"
                           "  ------------------------"
                           "  admin         .  .  .  ."
                           "  database1     .  .  .  x"
                           "  test-helper1  .  .  .  ."
                           "  user1         t  t  .  ."
                           "  util1         .  .  .  ."]}
               {:cmd "deps"
                :args ["project:s"]
                :expected ["                   t   "
                           "                   e   "
                           "                   s   "
                           "                d  t   "
                           "                a  -   "
                           "                t  h   "
                           "                a  e   "
                           "                b  l  u"
                           "                a  p  t"
                           "                s  e  i"
                           "                e  r  l"
                           "  brick         1  1  1"
                           "  ---------------------"
                           "  database1     .  .  x"
                           "  test-helper1  .  .  ."
                           "  user1         t  t  -"
                           "  util1         .  .  ."]}
               {:cmd "deps"
                :args ["brick:database1"]
                :expected ["  used by    <  database1  >  uses"
                           "  ---------                   ----"
                           "  user1 (t)                   util"]}
               {:cmd "deps"
                :args ["project:s" "brick:database1"]
                :expected ["  used by    <  database1  >  uses "
                           "  ---------                   -----"
                           "  user1 (t)                   util1"]}
               {:cmd "libs"
                :args []
                :expected ["                                                                                   t"
                           "                                                                                   e"
                           "                                                                                   s"
                           "                                                                                   t"
                           "                                                                                   -"
                           "                                                                                   h"
                           "                                                                                   e"
                           "                                                                                   l"
                           "                                                                                   p"
                           "                                                                                   e"
                           "                                                                                   r"
                           "  library                       version   type      KB   s   dev  default  extra   1"
                           "  ----------------------------------------------------   -   -------------------   -"
                           "  me.raynes/fs                  1.4.6     maven     10   -    -      x       -     ."
                           "  metosin/malli                 0.5.0     maven     42   t    x      -       -     x"
                           "  org.clojure/clojure           1.10.1    maven  3,816   x    x      -       -     ."
                           "  org.clojure/tools.deps.alpha  0.12.985  maven     59   x    x      -       -     ."]}
               {:cmd "libs"
                :args ["skip:dev"]
                :expected ["                                                             t"
                           "                                                             e"
                           "                                                             s"
                           "                                                             t"
                           "                                                             -"
                           "                                                             h"
                           "                                                             e"
                           "                                                             l"
                           "                                                             p"
                           "                                                             e"
                           "                                                             r"
                           "  library                       version   type      KB   s   1"
                           "  ----------------------------------------------------   -   -"
                           "  me.raynes/fs                  1.4.6     maven     10   -   ."
                           "  metosin/malli                 0.5.0     maven     42   t   x"
                           "  org.clojure/clojure           1.10.1    maven  3,816   x   ."
                           "  org.clojure/tools.deps.alpha  0.12.985  maven     59   x   ."]}
               {:cmd             "ws"
                :args            ["get:settings" "branch:create-deps-files"]
                :clean-result-fn clean-get-settings-result
                :expected        {:active-profiles      #{"default"}
                                  :color-mode           "none"
                                  :compact-views        #{}
                                  :default-profile-name "default"
                                  :empty-character      "."
                                  :interface-ns         "interface"
                                  :m2-dir               (str (System/getProperty "user.home") "/.m2")
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
                                  :vcs                  {:git-root (System/getProperty "user.dir")
                                                         :name     "git"
                                                         :auto-add true
                                                         :is-git-repo true
                                                         :polylith {:branch "create-deps-files"
                                                                    :repo   "https://github.com/polyfy/polylith.git"}}}}])

(deftest run-profile-tests
  (doseq [{:keys [expected actual]} (profile-ws/execute-commands commands)]
    (is (= expected (if (string? actual)
                      (str/split-lines actual)
                      actual)))))
