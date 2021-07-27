(ns project.poly.workspace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.text-table.brick-deps-table :as brick-ifc-deps]
            [polylith.clj.core.deps.text-table.project-brick-deps-table :as brick-deps-table]
            [polylith.clj.core.deps.text-table.workspace-deps-table :as ws-ifc-deps-table]
            [polylith.clj.core.deps.text-table.project-deps-table :as ws-project-deps-table]
            [polylith.clj.core.lib.text-table.lib-table :as libs]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.workspace.text-table.project-table :as project-table]
            [polylith.clj.core.workspace.text-table.ws-table :as ws-table]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.ws-explorer.core :as ws-explorer]))

(defn workspace [& args]
  (-> (user-input/extract-params (concat ["info" (str "ws-dir:.") "color-mode:none" "since:0aaeb58"] args))
      ws-clj/workspace-from-disk
      ws/enrich-workspace
      change/with-changes))

(deftest project-table
  (is (= ["  project        alias  status   dev"
          "  ----------------------------   ---"
          "  api *          api     ---     ---"
          "  core *         core    ---     ---"
          "  deployer *     depl    ---     ---"
          "  poly *         poly    -t-     -t-"
          "  development *  dev     s--     s--"]
         (project-table/table (workspace) false false))))

(deftest info
  (is (= ["  interface      brick             api  core  depl  poly   dev"
          "  ------------------------------   ---------------------   ---"
          "  api            api *             s--  ---   s--   ---    st-"
          "  change         change *          s--  s--   s--   stx    st-"
          "  command        command *         ---  ---   ---   stx    st-"
          "  common         common *          s--  s--   s--   s--    s--"
          "  creator        creator *         ---  ---   ---   stx    st-"
          "  deployer       deployer *        ---  ---   s--   ---    s--"
          "  deps           deps *            s--  s--   s--   stx    st-"
          "  file           file *            s--  s--   s--   stx    st-"
          "  git            git *             s--  s--   s--   stx    st-"
          "  help           help *            ---  ---   ---   s--    s--"
          "  lib            lib *             s--  s--   s--   stx    st-"
          "  migrator       migrator *        ---  ---   ---   stx    st-"
          "  path-finder    path-finder *     s--  s--   s--   stx    st-"
          "  shell          shell *           s--  s--   s--   s--    s--"
          "  test-helper    test-helper *     ---  ---   ---   -tx    s--"
          "  test-runner    test-runner *     ---  ---   ---   stx    st-"
          "  text-table     text-table *      s--  s--   s--   s--    s--"
          "  user-config    user-config *     s--  s--   s--   s--    s--"
          "  user-input     user-input *      s--  s--   s--   stx    st-"
          "  util           util *            s--  s--   s--   stx    st-"
          "  validator      validator *       s--  s--   s--   stx    st-"
          "  version        version *         s--  ---   s--   s--    s--"
          "  workspace      workspace *       s--  s--   s--   stx    st-"
          "  workspace-clj  workspace-clj *   s--  ---   s--   stx    st-"
          "  ws-explorer    ws-explorer *     s--  ---   s--   stx    st-"
          "  ws-file        ws-file *         ---  ---   ---   s--    s--"
          "  -              deployer-cli *    ---  ---   s--   ---    s--"
          "  -              migrator-cli *    ---  ---   ---   ---    s--"
          "  -              poly-cli *        ---  ---   ---   s--    s--"]
         (ws-table/table (workspace) false false))))

(deftest libs
  (is (= ["                                                                                                           w   "
          "                                                                                                           o   "
          "                                                                                                           r  w"
          "                                                                                                           k  s"
          "                                                                                                        v  s  -"
          "                                                                                            d        m  a  p  e"
          "                                                                                            e        i  l  a  x"
          "                                                                                            p        g  i  c  p"
          "                                                                                            l        r  d  e  l"
          "                                                                                            o  d  f  a  a  -  o"
          "                                                                                            y  e  i  t  t  c  r"
          "                                                                                            e  p  l  o  o  l  e"
          "  library                       version        type      KB   api  core  depl  poly   dev   r  s  e  r  r  j  r"
          "  ---------------------------------------------------------   ---------------------   ---   -------------------"
          "  me.raynes/fs                  1.4.6          maven     10    x    x     x     x      x    .  .  x  .  .  .  ."
          "  metosin/malli                 0.5.1          maven     42    x    x     x     x      x    .  .  .  .  x  .  ."
          "  mount/mount                   0.1.16         maven      8    -    -     -     -      x    .  .  .  .  .  .  ."
          "  mvxcvi/puget                  1.3.1          maven     15    x    -     x     x      x    .  .  .  .  .  .  x"
          "  org.clojure/clojure           1.10.3         maven  3,822    x    x     x     x      x    .  .  .  .  .  .  ."
          "  org.clojure/tools.deps.alpha  0.12.1003      maven     59    x    x     x     x      x    .  x  .  .  .  x  ."
          "  org.slf4j/slf4j-nop           1.7.32         maven      3    -    -     x     x      x    .  .  .  .  .  .  ."
          "  rewrite-clj/rewrite-clj       1.0.644-alpha  maven     71    -    -     -     -      x    .  .  .  .  .  .  ."
          "  slipset/deps-deploy           0.1.5          maven      6    -    -     x     -      x    x  .  .  .  .  .  ."
          "  zprint/zprint                 1.1.2          maven    164    -    -     -     x      x    .  .  .  x  .  .  ."]
         (libs/table (workspace) false))))

(deftest ifc-deps-table
  (is (= ["                                                                                      w      "
          "                                                                                      o      "
          "                                                     p     t  t     u                 r  w   "
          "                                                     a     e  e  t  s  u              k  s   "
          "                                                     t     s  s  e  e  s     v     w  s  -   "
          "                                d                 m  h     t  t  x  r  e     a     o  p  e   "
          "                       c     c  e                 i  -     -  -  t  -  r     l  v  r  a  x  w"
          "                    c  o  c  r  p                 g  f     h  r  -  c  -     i  e  k  c  p  s"
          "                    h  m  o  e  l                 r  i  s  e  u  t  o  i     d  r  s  e  l  -"
          "                    a  m  m  a  o  d  f     h     a  n  h  l  n  a  n  n  u  a  s  p  -  o  f"
          "                 a  n  a  m  t  y  e  i  g  e  l  t  d  e  p  n  b  f  p  t  t  i  a  c  r  i"
          "                 p  g  n  o  o  e  p  l  i  l  i  o  e  l  e  e  l  i  u  i  o  o  c  l  e  l"
          "  brick          i  e  d  n  r  r  s  e  t  p  b  r  r  l  r  r  e  g  t  l  r  n  e  j  r  e"
          "  -------------------------------------------------------------------------------------------"
          "  api            .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  x  x  x  ."
          "  change         .  .  .  .  .  .  .  .  x  .  .  .  x  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  command        .  x  .  x  x  .  x  x  x  x  x  x  .  .  .  x  .  x  x  x  x  x  x  x  x  x"
          "  common         .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  x  .  x  .  .  .  .  .  ."
          "  creator        .  .  .  x  .  .  .  x  x  .  .  .  .  .  t  .  .  .  .  x  .  .  .  .  .  ."
          "  deployer       x  .  .  .  .  .  .  x  .  .  .  .  .  x  .  .  .  .  .  .  .  x  .  .  .  ."
          "  deps           .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  x  .  .  .  .  .  ."
          "  file           .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  git            .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  x  .  .  .  .  .  ."
          "  help           .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  x  .  .  .  ."
          "  lib            .  .  .  x  .  .  .  x  .  .  .  .  .  .  t  .  x  x  .  x  .  .  .  .  .  ."
          "  migrator       .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  path-finder    .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  shell          .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  test-helper    .  .  x  .  .  .  .  x  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  .  .  ."
          "  test-runner    .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  ."
          "  text-table     .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  user-config    .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  user-input     .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  util           .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  validator      .  .  .  x  .  .  x  .  .  .  .  .  x  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  version        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  workspace      .  .  .  x  .  .  x  x  .  .  .  .  x  .  t  .  x  .  .  x  x  .  .  .  .  ."
          "  workspace-clj  .  .  .  x  .  .  .  x  x  .  x  .  x  .  .  .  .  x  .  x  x  x  .  .  .  ."
          "  ws-explorer    .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  x  .  .  .  .  .  ."
          "  ws-file        .  .  .  x  .  .  .  x  x  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  ."
          "  deployer-cli   .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  migrator-cli   .  .  .  .  .  .  .  x  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  poly-cli       .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  .  ."]
         (ws-ifc-deps-table/table (workspace)))))

(deftest project-deps-table
  (let [ws (workspace)
        projects (:projects ws)
        project (common/find-project "poly" projects)]
    (is (= ["                                                                                w      "
            "                                                                                o      "
            "                                               p     t  t     u                 r  w   "
            "                                               a     e  e  t  s  u              k  s   "
            "                                               t     s  s  e  e  s     v     w  s  -   "
            "                                            m  h     t  t  x  r  e     a     o  p  e   "
            "                    c     c                 i  -     -  -  t  -  r     l  v  r  a  x  w"
            "                 c  o  c  r                 g  f     h  r  -  c  -     i  e  k  c  p  s"
            "                 h  m  o  e                 r  i  s  e  u  t  o  i     d  r  s  e  l  -"
            "                 a  m  m  a  d  f     h     a  n  h  l  n  a  n  n  u  a  s  p  -  o  f"
            "                 n  a  m  t  e  i  g  e  l  t  d  e  p  n  b  f  p  t  t  i  a  c  r  i"
            "                 g  n  o  o  p  l  i  l  i  o  e  l  e  e  l  i  u  i  o  o  c  l  e  l"
            "  brick          e  d  n  r  s  e  t  p  b  r  r  l  r  r  e  g  t  l  r  n  e  j  r  e"
            "  -------------------------------------------------------------------------------------"
            "  change         .  .  .  .  .  +  x  .  .  .  x  +  .  .  .  .  .  x  .  .  .  .  .  ."
            "  command        x  .  x  x  x  x  x  x  x  x  +  +  .  x  +  x  x  x  x  x  x  x  x  x"
            "  common         .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  x  .  x  .  .  .  .  .  ."
            "  creator        -  -  x  -  -  x  x  -  -  -  -  +  t  -  -  +  -  x  -  -  -  -  -  -"
            "  deps           .  .  x  .  .  +  .  .  .  .  .  .  .  .  x  +  .  x  .  .  .  .  .  ."
            "  file           .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  git            .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  x  .  .  .  .  .  ."
            "  help           .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  x  .  .  .  ."
            "  lib            -  -  x  -  -  x  -  -  -  -  -  -  t  -  x  x  -  x  -  -  -  -  -  -"
            "  migrator       .  .  x  .  .  +  .  .  .  .  .  .  .  .  .  +  .  +  .  .  .  .  .  ."
            "  path-finder    .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  shell          .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  test-helper    -  t  -  -  -  t  -  -  -  -  -  -  .  -  -  t  t  -  -  -  -  -  -  -"
            "  test-runner    .  .  x  .  x  +  .  .  .  .  +  .  .  .  +  +  .  x  x  .  .  .  .  ."
            "  text-table     .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  user-config    .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  user-input     .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  util           .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  validator      .  .  x  .  x  +  .  .  .  .  x  .  .  .  +  +  .  x  .  .  .  .  .  ."
            "  version        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  workspace      -  -  x  -  x  x  -  -  -  -  x  -  t  -  x  +  -  x  x  -  -  -  -  -"
            "  workspace-clj  .  .  x  .  +  x  x  .  x  .  x  +  .  .  +  x  .  x  x  x  .  .  .  ."
            "  ws-explorer    .  .  .  .  .  +  .  .  .  .  .  .  .  .  .  x  .  x  .  .  .  .  .  ."
            "  ws-file        .  .  x  .  .  x  x  .  .  .  .  +  .  .  .  +  .  +  .  x  .  .  .  ."
            "  poly-cli       +  x  +  +  +  +  +  +  +  +  +  +  .  +  +  +  x  x  +  +  +  +  +  +"]
           (ws-project-deps-table/table (workspace) project false)))))

(deftest project-and-brick-deps
  (let [{:keys [components projects] :as ws} (workspace)
        project (common/find-project "poly" projects)
        brick (common/find-component "workspace" components)]
    (is (= ["  used by  <  workspace  >  uses           "
            "  -------                   ---------------"
            "  command                   common         "
            "                            deps           "
            "                            file           "
            "                            path-finder    "
            "                            test-helper (t)"
            "                            text-table     "
            "                            util           "
            "                            validator      "]
           (brick-deps-table/table ws project brick "none")))))

(deftest project-brick-deps
  (let [{:keys [components] :as ws} (workspace)
        brick (common/find-component "workspace" components)]
    (is (= ["  used by  <  workspace  >  uses           "
            "  -------                   ---------------"
            "  api                       common         "
            "  command                   deps           "
            "                            file           "
            "                            path-finder    "
            "                            test-helper (t)"
            "                            text-table     "
            "                            util           "
            "                            validator      "]
           (brick-ifc-deps/table ws brick)))))

(deftest poly-project-deps
  (is (= {"change"        {:src  {:direct   ["git"
                                             "path-finder"
                                             "util"]
                                  :indirect ["file"
                                             "shell"]}
                           :test {:direct   ["git"
                                             "path-finder"
                                             "util"]
                                  :indirect ["file"
                                             "shell"]}}
          "command"       {:src  {:direct   ["change"
                                             "common"
                                             "creator"
                                             "deps"
                                             "file"
                                             "git"
                                             "help"
                                             "lib"
                                             "migrator"
                                             "test-runner"
                                             "user-config"
                                             "user-input"
                                             "util"
                                             "validator"
                                             "version"
                                             "workspace"
                                             "workspace-clj"
                                             "ws-explorer"
                                             "ws-file"]
                                  :indirect ["path-finder"
                                             "shell"
                                             "text-table"]}
                           :test {:direct   ["change"
                                             "common"
                                             "creator"
                                             "deps"
                                             "file"
                                             "git"
                                             "help"
                                             "lib"
                                             "migrator"
                                             "test-runner"
                                             "user-config"
                                             "user-input"
                                             "util"
                                             "validator"
                                             "version"
                                             "workspace"
                                             "workspace-clj"
                                             "ws-explorer"
                                             "ws-file"]
                                  :indirect ["path-finder"
                                             "shell"
                                             "text-table"]}}
          "common"        {:src  {:direct ["file"
                                           "user-config"
                                           "util"]}
                           :test {}}
          "creator"       {:src  {:direct   ["common"
                                             "file"
                                             "git"
                                             "util"]
                                  :indirect ["shell"
                                             "user-config"]}
                           :test {:direct   ["common"
                                             "file"
                                             "git"
                                             "test-helper"
                                             "util"]
                                  :indirect ["change"
                                             "command"
                                             "creator"
                                             "deps"
                                             "help"
                                             "lib"
                                             "migrator"
                                             "path-finder"
                                             "shell"
                                             "test-runner"
                                             "text-table"
                                             "user-config"
                                             "user-input"
                                             "validator"
                                             "version"
                                             "workspace"
                                             "workspace-clj"
                                             "ws-explorer"
                                             "ws-file"]}}
          "deps"          {:src  {:direct   ["common"
                                             "text-table"
                                             "util"]
                                  :indirect ["file"
                                             "user-config"]}
                           :test {:direct   ["common"
                                             "text-table"
                                             "util"]
                                  :indirect ["file"
                                             "user-config"]}}
          "file"          {:src  {:direct ["util"]}
                           :test {}}
          "git"           {:src  {:direct ["shell"
                                           "util"]}
                           :test {:direct ["shell"
                                           "util"]}}
          "help"          {:src  {:direct ["util"
                                           "version"]}
                           :test {}}
          "lib"           {:src  {:direct ["common"
                                           "file"
                                           "text-table"
                                           "user-config"
                                           "util"]}
                           :test {:direct   ["common"
                                             "file"
                                             "test-helper"
                                             "text-table"
                                             "user-config"
                                             "util"]
                                  :indirect ["change"
                                             "command"
                                             "creator"
                                             "deps"
                                             "git"
                                             "help"
                                             "lib"
                                             "migrator"
                                             "path-finder"
                                             "shell"
                                             "test-runner"
                                             "user-input"
                                             "validator"
                                             "version"
                                             "workspace"
                                             "workspace-clj"
                                             "ws-explorer"
                                             "ws-file"]}}
          "migrator"      {:src  {:direct   ["common"]
                                  :indirect ["file"
                                             "user-config"
                                             "util"]}
                           :test {}}
          "path-finder"   {:src  {:direct ["file"
                                           "util"]}
                           :test {:direct ["file"
                                           "util"]}}
          "poly-cli"      {:src  {:direct   ["command"
                                             "user-input"
                                             "util"]
                                  :indirect ["change"
                                             "common"
                                             "creator"
                                             "deps"
                                             "file"
                                             "git"
                                             "help"
                                             "lib"
                                             "migrator"
                                             "path-finder"
                                             "shell"
                                             "test-runner"
                                             "text-table"
                                             "user-config"
                                             "validator"
                                             "version"
                                             "workspace"
                                             "workspace-clj"
                                             "ws-explorer"
                                             "ws-file"]}
                           :test {}}
          "shell"         {:src  {}
                           :test {}}
          "test-helper"   {:src  {}
                           :test {:direct   ["command"
                                             "file"
                                             "user-config"
                                             "user-input"]
                                  :indirect ["change"
                                             "common"
                                             "creator"
                                             "deps"
                                             "git"
                                             "help"
                                             "lib"
                                             "migrator"
                                             "path-finder"
                                             "shell"
                                             "test-runner"
                                             "text-table"
                                             "util"
                                             "validator"
                                             "version"
                                             "workspace"
                                             "workspace-clj"
                                             "ws-explorer"
                                             "ws-file"]}}
          "test-runner"   {:src  {:direct   ["common"
                                             "deps"
                                             "util"
                                             "validator"]
                                  :indirect ["file"
                                             "path-finder"
                                             "text-table"
                                             "user-config"]}
                           :test {}}
          "text-table"    {:src  {:direct ["util"]}
                           :test {}}
          "user-config"   {:src  {:direct ["file"
                                           "util"]}
                           :test {}}
          "user-input"    {:src  {:direct ["util"]}
                           :test {:direct ["util"]}}
          "util"          {:src  {}
                           :test {}}
          "validator"     {:src  {:direct   ["common"
                                             "deps"
                                             "path-finder"
                                             "util"]
                                  :indirect ["file"
                                             "text-table"
                                             "user-config"]}
                           :test {:direct   ["common"
                                             "deps"
                                             "path-finder"
                                             "util"]
                                  :indirect ["file"
                                             "text-table"
                                             "user-config"]}}
          "version"       {:src  {}
                           :test {}}
          "workspace"     {:src  {:direct   ["common"
                                             "deps"
                                             "file"
                                             "path-finder"
                                             "text-table"
                                             "util"
                                             "validator"]
                                  :indirect ["user-config"]}
                           :test {:direct   ["common"
                                             "deps"
                                             "file"
                                             "path-finder"
                                             "test-helper"
                                             "text-table"
                                             "util"
                                             "validator"]
                                  :indirect ["change"
                                             "command"
                                             "creator"
                                             "git"
                                             "help"
                                             "lib"
                                             "migrator"
                                             "shell"
                                             "test-runner"
                                             "user-config"
                                             "user-input"
                                             "version"
                                             "workspace"
                                             "workspace-clj"
                                             "ws-explorer"
                                             "ws-file"]}}
          "workspace-clj" {:src  {:direct   ["common"
                                             "file"
                                             "git"
                                             "lib"
                                             "path-finder"
                                             "user-config"
                                             "util"
                                             "validator"
                                             "version"]
                                  :indirect ["deps"
                                             "shell"
                                             "text-table"]}
                           :test {:direct   ["common"
                                             "file"
                                             "git"
                                             "lib"
                                             "path-finder"
                                             "user-config"
                                             "util"
                                             "validator"
                                             "version"]
                                  :indirect ["deps"
                                             "shell"
                                             "text-table"]}}
          "ws-explorer"   {:src  {:direct   ["user-config"
                                             "util"]
                                  :indirect ["file"]}
                           :test {:direct   ["user-config"
                                             "util"]
                                  :indirect ["file"]}}
          "ws-file"       {:src  {:direct   ["common"
                                             "file"
                                             "git"
                                             "version"]
                                  :indirect ["shell"
                                             "user-config"
                                             "util"]}
                           :test {}}}
         (ws-explorer/extract (workspace) ["projects" "poly" "deps"]))))

(deftest poly-project-src-paths
  (is (= ["bases/poly-cli/src"
          "components/change/src"
          "components/command/src"
          "components/common/src"
          "components/creator/resources"
          "components/creator/src"
          "components/deps/src"
          "components/file/src"
          "components/git/src"
          "components/help/src"
          "components/lib/src"
          "components/migrator/src"
          "components/path-finder/src"
          "components/shell/src"
          "components/test-runner/src"
          "components/text-table/src"
          "components/user-config/src"
          "components/user-input/src"
          "components/util/src"
          "components/validator/src"
          "components/version/src"
          "components/workspace-clj/src"
          "components/workspace/src"
          "components/ws-explorer/src"
          "components/ws-file/src"]
         (ws-explorer/extract (workspace) ["projects" "poly" "paths" "src"]))))

(deftest poly-project-test-paths
  (is (= ["components/change/test"
          "components/command/test"
          "components/creator/test"
          "components/deps/test"
          "components/file/test"
          "components/git/test"
          "components/lib/test"
          "components/migrator/test"
          "components/path-finder/test"
          "components/test-helper/src"
          "components/test-runner/test"
          "components/user-input/test"
          "components/util/test"
          "components/validator/test"
          "components/workspace-clj/test"
          "components/workspace/test"
          "components/ws-explorer/test"
          "projects/poly/test"]
         (ws-explorer/extract (workspace) ["projects" "poly" "paths" "test"]))))

(deftest poly-project-lib-imports
  (is (= {:src  ["clojure.java.io"
                 "clojure.java.shell"
                 "clojure.pprint"
                 "clojure.set"
                 "clojure.stacktrace"
                 "clojure.string"
                 "clojure.tools.deps.alpha"
                 "clojure.tools.deps.alpha.util.maven"
                 "clojure.walk"
                 "java.io"
                 "java.net"
                 "java.nio.file"
                 "java.util"
                 "malli.core"
                 "malli.error"
                 "me.raynes.fs"
                 "org.eclipse.aether.util.version"
                 "puget.printer"]
          :test ["clojure.test"]}
         (ws-explorer/extract (workspace) ["projects" "poly" "lib-imports"]))))

(deftest poly-project-lib-imports
  (is (= {:src {"slipset/deps-deploy" {:size    6205
                                       :type    "maven"
                                       :version "0.1.5"}}}
         (ws-explorer/extract (workspace) ["components" "deployer" "lib-deps"]))))
