(ns project.poly.poly-workspace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.text-table.brick-deps-table :as brick-ifc-deps]
            [polylith.clj.core.deps.text-table.project-brick-deps-table :as brick-deps-table]
            [polylith.clj.core.deps.text-table.project-deps-table :as ws-project-deps-table]
            [polylith.clj.core.deps.text-table.workspace-deps-table :as ws-ifc-deps-table]
            [polylith.clj.core.lib.text-table.lib-table :as libs]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.workspace.text-table.project-table :as project-table]
            [polylith.clj.core.workspace.text-table.ws-table :as ws-table]
            [polylith.clj.core.ws-explorer.core :as ws-explorer]))

(defn workspace [& args]
  (-> (user-input/extract-params (concat ["info" (str "ws-dir:.") "color-mode:none" "since:0aaeb58"] args))
      ws-clj/workspace-from-disk
      ws/enrich-workspace
      change/with-changes))

(deftest project-table
  (is (= (project-table/table (workspace) false false)
         ["  project        alias  status   dev"
          "  ----------------------------   ---"
          "  api *          api     ---     ---"
          "  poly *         poly    -t-     -t-"
          "  development *  dev     s--     s--"])))

(deftest info
  (is (= (ws-table/table (workspace) false false)
         ["  interface                 brick                        api  poly   dev"
          "  ----------------------------------------------------   ---------   ---"
          "  api                       api *                        s--  ---    st-"
          "  change                    change *                     s--  stx    st-"
          "  clojure-test-test-runner  clojure-test-test-runner *   ---  stx    st-"
          "  command                   command *                    ---  stx    st-"
          "  common                    common *                     s--  stx    st-"
          "  config-reader             config-reader *              s--  stx    st-"
          "  creator                   creator *                    ---  stx    st-"
          "  deps                      deps *                       s--  stx    st-"
          "  file                      file *                       s--  stx    st-"
          "  git                       git *                        s--  stx    st-"
          "  help                      help *                       ---  s--    s--"
          "  lib                       lib *                        s--  stx    st-"
          "  migrator                  migrator *                   ---  stx    st-"
          "  path-finder               path-finder *                s--  stx    st-"
          "  sh                        sh *                         s--  s--    s--"
          "  shell                     shell *                      ---  stx    st-"
          "  tap                       tap *                        ---  s--    s--"
          "  test-helper               test-helper *                ---  -tx    s--"
          "  test-runner-contract      test-runner-contract *       s--  stx    st-"
          "  test-runner-orchestrator  test-runner-orchestrator *   ---  stx    st-"
          "  text-table                text-table *                 s--  s--    s--"
          "  user-config               user-config *                s--  s--    s--"
          "  user-input                user-input *                 s--  stx    st-"
          "  util                      util *                       s--  stx    st-"
          "  validator                 validator *                  s--  stx    st-"
          "  version                   version *                    s--  s--    s--"
          "  workspace                 workspace *                  s--  stx    st-"
          "  workspace-clj             workspace-clj *              s--  stx    st-"
          "  ws-explorer               ws-explorer *                s--  stx    st-"
          "  ws-file                   ws-file *                    ---  s--    s--"
          "  -                         poly-cli *                   ---  stx    st-"])))

(deftest libs
  (is (= (libs/table (workspace) false)
         ["                                                                                                  w   "
          "                                                                                                  o   "
          "                                                                                                  r  w"
          "                                                                                                  k  s"
          "                                                                                               v  s  -"
          "                                                                                      m        a  p  e"
          "                                                                                      i        l  a  x"
          "                                                                                      g        i  c  p"
          "                                                                                      r  s     d  e  l"
          "                                                                                d  f  a  h     a  -  o"
          "                                                                                e  i  t  e  t  t  c  r"
          "                                                                                p  l  o  l  a  o  l  e"
          "  library                           version    type      KB   api  poly   dev   s  e  r  l  p  r  j  r"
          "  ---------------------------------------------------------   ---------   ---   ----------------------"
          "  borkdude/edamame                  1.3.20     maven     24    x    x      x    .  x  .  .  .  .  .  ."
          "  clj-commons/fs                    1.6.310    maven     12    x    x      x    .  x  .  .  .  .  .  ."
          "  djblue/portal                     0.37.1     maven  1,818    -    x      x    .  .  .  .  x  .  .  ."
          "  io.github.seancorfield/build-clj  9c9f078    git       42    -    -      x    .  .  .  .  .  .  .  ."
          "  metosin/malli                     0.10.4     maven     85    x    x      x    .  .  .  .  .  x  .  ."
          "  mount/mount                       0.1.17     maven      8    -    -      x    .  .  .  .  .  .  .  ."
          "  mvxcvi/puget                      1.3.4      maven     15    x    x      x    .  .  .  .  .  .  .  x"
          "  org.clojure/clojure               1.11.1     maven  4,008    x    x      x    .  .  .  .  .  .  .  ."
          "  org.clojure/tools.deps            0.18.1308  maven     57    x    x      x    x  x  .  .  .  .  x  ."
          "  org.jline/jline                   3.21.0     maven    971    -    x      x    .  .  .  x  .  .  .  ."
          "  org.slf4j/slf4j-nop               2.0.7      maven      4    -    x      x    .  .  .  .  .  .  .  ."
          "  rewrite-clj/rewrite-clj           1.1.47     maven     73    -    -      x    .  .  .  .  .  .  .  ."
          "  slipset/deps-deploy               0.2.0      maven      7    -    -      x    .  .  .  .  .  .  .  ."
          "  zprint/zprint                     1.2.5      maven    195    -    x      x    .  .  x  .  .  .  .  ."])))

(deftest ifc-deps-table
  (is (= (ws-ifc-deps-table/table (workspace))
         ["                                  c                                                  t                              "
          "                                  l                                                  e                              "
          "                                  o                                                  s                              "
          "                                  j                                                  t                              "
          "                                  u                                               t  -                              "
          "                                  r                                               e  r                              "
          "                                  e                                               s  u                              "
          "                                  -                                               t  n                              "
          "                                  t                                               -  n                              "
          "                                  e                                               r  e                              "
          "                                  s                                               u  r                              "
          "                                  t        c                                      n  -                       w      "
          "                                  -        o                                      n  o                       o      "
          "                                  t        n                       p           t  e  r     u                 r  w   "
          "                                  e        f                       a           e  r  c  t  s  u              k  s   "
          "                                  s        i                       t           s  -  h  e  e  s     v     w  s  -   "
          "                                  t        g                    m  h           t  c  e  x  r  e     a     o  p  e   "
          "                                  -  c     -  c                 i  -           -  o  s  t  -  r     l  v  r  a  x  w"
          "                               c  r  o  c  r  r                 g  f           h  n  t  -  c  -     i  e  k  c  p  s"
          "                               h  u  m  o  e  e                 r  i     s     e  t  r  t  o  i     d  r  s  e  l  -"
          "                               a  n  m  m  a  a  d  f     h     a  n     h     l  r  a  a  n  n  u  a  s  p  -  o  f"
          "                            a  n  n  a  m  d  t  e  i  g  e  l  t  d     e  t  p  a  t  b  f  p  t  t  i  a  c  r  i"
          "                            p  g  e  n  o  e  o  p  l  i  l  i  o  e  s  l  a  e  c  o  l  i  u  i  o  o  c  l  e  l"
          "  brick                     i  e  r  d  n  r  r  s  e  t  p  b  r  r  h  l  p  r  t  r  e  g  t  l  r  n  e  j  r  e"
          "  ------------------------------------------------------------------------------------------------------------------"
          "  api                       .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  x  x  x  ."
          "  change                    .  .  .  .  x  .  .  .  .  x  .  .  .  x  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  clojure-test-test-runner  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  ."
          "  command                   .  x  .  .  x  x  x  x  x  x  x  x  x  .  .  x  x  .  .  x  .  x  .  x  x  x  x  x  x  x"
          "  common                    .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  x  .  .  .  .  .  ."
          "  config-reader             .  .  .  .  x  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  ."
          "  creator                   .  .  .  .  x  .  .  .  x  x  .  .  .  .  .  .  .  t  .  .  .  .  .  x  .  .  .  .  .  ."
          "  deps                      .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  x  .  .  .  .  .  ."
          "  file                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  git                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  help                      .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  x  .  .  .  ."
          "  lib                       .  .  .  .  x  x  .  .  x  .  .  .  .  .  .  .  .  t  .  .  x  x  .  x  .  .  .  .  .  ."
          "  migrator                  .  .  .  .  x  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  path-finder               .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  sh                        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  shell                     .  .  .  .  x  x  .  .  x  .  .  .  .  .  x  .  x  .  .  .  .  x  x  x  .  x  .  .  x  ."
          "  tap                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  test-helper               .  .  .  x  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  .  .  ."
          "  test-runner-contract      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  test-runner-orchestrator  .  .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  x  .  .  .  .  ."
          "  text-table                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  user-config               .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  user-input                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  util                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  validator                 .  .  .  .  x  .  .  x  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  ."
          "  version                   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  workspace                 .  .  .  .  x  .  .  x  x  .  .  .  .  x  .  .  .  t  .  .  x  .  .  x  x  .  .  .  .  ."
          "  workspace-clj             .  .  .  .  x  x  .  x  x  x  .  x  .  x  .  .  .  .  .  .  .  x  .  x  x  x  .  .  .  ."
          "  ws-explorer               .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  ws-file                   .  .  .  .  x  .  .  .  x  x  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  x  .  .  .  ."
          "  poly-cli                  .  .  .  x  .  t  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  t  .  .  t  .  ."])))

(deftest project-deps-table
  (let [ws (workspace)
        projects (:projects ws)
        project (common/find-project "poly" projects)]
    (is (= (ws-project-deps-table/table (workspace) project false)
           ["                                                                               t                              "
            "                                                                               e                              "
            "                                                                               s                              "
            "                                                                               t                              "
            "                                                                            t  -                              "
            "                                                                            e  r                              "
            "                                                                            s  u                              "
            "                                                                            t  n                              "
            "                                                                            -  n                              "
            "                                                                            r  e                              "
            "                                                                            u  r                              "
            "                                     c                                      n  -                       w      "
            "                                     o                                      n  o                       o      "
            "                                     n                       p           t  e  r     u                 r  w   "
            "                                     f                       a           e  r  c  t  s  u              k  s   "
            "                                     i                       t           s  -  h  e  e  s     v     w  s  -   "
            "                                     g                    m  h           t  c  e  x  r  e     a     o  p  e   "
            "                               c     -  c                 i  -           -  o  s  t  -  r     l  v  r  a  x  w"
            "                            c  o  c  r  r                 g  f           h  n  t  -  c  -     i  e  k  c  p  s"
            "                            h  m  o  e  e                 r  i     s     e  t  r  t  o  i     d  r  s  e  l  -"
            "                            a  m  m  a  a  d  f     h     a  n     h     l  r  a  a  n  n  u  a  s  p  -  o  f"
            "                            n  a  m  d  t  e  i  g  e  l  t  d     e  t  p  a  t  b  f  p  t  t  i  a  c  r  i"
            "                            g  n  o  e  o  p  l  i  l  i  o  e  s  l  a  e  c  o  l  i  u  i  o  o  c  l  e  l"
            "  brick                     e  d  n  r  r  s  e  t  p  b  r  r  h  l  p  r  t  r  e  g  t  l  r  n  e  j  r  e"
            "  ------------------------------------------------------------------------------------------------------------"
            "  change                    .  .  x  .  .  .  +  x  .  .  .  x  +  .  .  .  .  .  .  +  .  x  .  .  .  .  .  ."
            "  clojure-test-test-runner  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  ."
            "  command                   x  .  x  x  x  x  x  x  x  x  x  +  +  x  x  .  +  x  +  x  +  x  x  x  x  x  x  x"
            "  common                    .  .  .  .  .  .  +  .  .  .  .  .  .  .  .  .  .  .  .  x  .  x  .  .  .  .  .  ."
            "  config-reader             .  .  x  .  .  +  x  .  .  .  .  +  .  .  .  .  +  .  +  +  .  x  x  .  .  .  .  ."
            "  creator                   -  -  x  -  -  -  x  x  -  -  -  -  +  -  -  t  -  -  -  +  -  x  -  -  -  -  -  -"
            "  deps                      .  .  x  .  .  .  +  .  .  .  .  .  .  .  .  .  .  .  x  x  .  x  .  .  .  .  .  ."
            "  file                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  git                       .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  help                      .  .  x  .  .  .  +  .  .  .  .  .  .  .  .  .  .  .  .  +  .  x  .  x  .  .  .  ."
            "  lib                       -  -  x  x  -  +  x  -  -  -  -  +  -  -  -  t  +  -  x  x  -  x  +  -  -  -  -  -"
            "  migrator                  .  .  x  x  .  +  +  .  .  .  .  +  .  .  .  .  +  .  +  +  .  +  +  .  .  .  .  ."
            "  path-finder               .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  sh                        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  shell                     .  .  x  x  .  +  x  .  .  .  .  +  x  .  x  .  +  .  +  x  x  x  +  x  .  .  x  ."
            "  tap                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  test-helper               -  t  -  -  -  -  t  -  -  -  -  -  -  -  -  .  -  -  -  t  t  -  -  -  -  -  -  -"
            "  test-runner-contract      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  test-runner-orchestrator  .  .  x  .  .  x  +  .  .  .  .  +  .  .  .  .  x  .  +  +  .  x  x  .  .  .  .  ."
            "  text-table                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  user-config               .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  user-input                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  util                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  validator                 .  .  x  .  .  x  +  .  .  .  .  x  .  .  .  .  x  .  +  +  .  x  .  .  .  .  .  ."
            "  version                   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  workspace                 -  -  x  -  -  x  x  -  -  -  -  x  -  -  -  t  +  -  x  +  -  x  x  -  -  -  -  -"
            "  workspace-clj             .  .  x  x  .  x  x  x  .  x  .  x  +  .  .  .  +  .  +  x  .  x  x  x  .  .  .  ."
            "  ws-explorer               .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  ws-file                   .  .  x  .  .  .  x  x  .  .  .  .  +  .  .  .  .  .  .  +  .  x  .  x  .  .  .  ."
            "  poly-cli                  +  x  +  t  +  +  +  +  +  +  +  +  +  +  +  .  +  +  +  +  x  x  t  +  +  t  +  +"]))))

(deftest project-and-brick-deps
  (let [{:keys [components projects] :as ws} (workspace)
        project (common/find-project "poly" projects)
        brick (common/find-component "workspace" components)]
    (is (= (brick-deps-table/table ws project brick "none")
           ["  used by  <  workspace  >  uses           "
            "  -------                   ---------------"
            "  command                   common         "
            "                            deps           "
            "                            file           "
            "                            path-finder    "
            "                            test-helper (t)"
            "                            text-table     "
            "                            util           "
            "                            validator      "]))))

(deftest project-brick-deps
  (let [{:keys [components] :as ws} (workspace)
        brick (common/find-component "workspace" components)]
    (is (= (brick-ifc-deps/table ws brick)
           ["  used by  <  workspace  >  uses           "
            "  -------                   ---------------"
            "  api                       common         "
            "  command                   deps           "
            "                            file           "
            "                            path-finder    "
            "                            test-helper (t)"
            "                            text-table     "
            "                            util           "
            "                            validator      "]))))

(deftest poly-project-deps
  (is (= (ws-explorer/extract (workspace) ["projects" "poly" "deps"])
         {"change"                   {:src  {:direct   ["common"
                                                        "git"
                                                        "path-finder"
                                                        "util"]
                                             :indirect ["file"
                                                        "sh"
                                                        "user-config"]}
                                      :test {:direct   ["common"
                                                        "git"
                                                        "path-finder"
                                                        "util"]
                                             :indirect ["file"
                                                        "sh"
                                                        "user-config"]}}
          "clojure-test-test-runner" {:src  {:direct ["test-runner-contract"
                                                      "util"]}
                                      :test {:direct ["test-runner-contract"
                                                      "util"]}}
          "command"                  {:src  {:direct   ["change"
                                                        "common"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "file"
                                                        "git"
                                                        "help"
                                                        "lib"
                                                        "migrator"
                                                        "shell"
                                                        "tap"
                                                        "test-runner-orchestrator"
                                                        "user-config"
                                                        "util"
                                                        "validator"
                                                        "version"
                                                        "workspace"
                                                        "workspace-clj"
                                                        "ws-explorer"
                                                        "ws-file"]
                                             :indirect ["path-finder"
                                                        "sh"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-input"]}
                                      :test {:direct   ["change"
                                                        "common"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "file"
                                                        "git"
                                                        "help"
                                                        "lib"
                                                        "migrator"
                                                        "shell"
                                                        "tap"
                                                        "test-runner-orchestrator"
                                                        "user-config"
                                                        "util"
                                                        "validator"
                                                        "version"
                                                        "workspace"
                                                        "workspace-clj"
                                                        "ws-explorer"
                                                        "ws-file"]
                                             :indirect ["path-finder"
                                                        "sh"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-input"]}}
          "common"                   {:src  {:direct   ["user-config"
                                                        "util"]
                                             :indirect ["file"]}
                                      :test {}}
          "config-reader"            {:src  {:direct   ["common"
                                                        "file"
                                                        "util"
                                                        "validator"]
                                             :indirect ["deps"
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-config"]}
                                      :test {:direct   ["common"
                                                        "file"
                                                        "util"
                                                        "validator"]
                                             :indirect ["deps"
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-config"]}}
          "creator"                  {:src  {:direct   ["common"
                                                        "file"
                                                        "git"
                                                        "util"]
                                             :indirect ["sh"
                                                        "user-config"]}
                                      :test {:direct   ["common"
                                                        "file"
                                                        "git"
                                                        "test-helper"
                                                        "util"]
                                             :indirect ["change"
                                                        "command"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "help"
                                                        "lib"
                                                        "migrator"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "tap"
                                                        "test-runner-contract"
                                                        "test-runner-orchestrator"
                                                        "text-table"
                                                        "user-config"
                                                        "user-input"
                                                        "validator"
                                                        "version"
                                                        "workspace"
                                                        "workspace-clj"
                                                        "ws-explorer"
                                                        "ws-file"]}}
          "deps"                     {:src  {:direct   ["common"
                                                        "text-table"
                                                        "user-config"
                                                        "util"]
                                             :indirect ["file"]}
                                      :test {:direct   ["common"
                                                        "text-table"
                                                        "user-config"
                                                        "util"]
                                             :indirect ["file"]}}
          "file"                     {:src  {:direct ["util"]}
                                      :test {}}
          "git"                      {:src  {:direct ["sh"
                                                      "util"]}
                                      :test {:direct ["sh"
                                                      "util"]}}
          "help"                     {:src  {:direct   ["common"
                                                        "util"
                                                        "version"]
                                             :indirect ["file"
                                                        "user-config"]}
                                      :test {}}
          "lib"                      {:src  {:direct   ["common"
                                                        "config-reader"
                                                        "file"
                                                        "text-table"
                                                        "user-config"
                                                        "util"]
                                             :indirect ["deps"
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "validator"]}
                                      :test {:direct   ["common"
                                                        "config-reader"
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
                                                        "sh"
                                                        "shell"
                                                        "tap"
                                                        "test-runner-contract"
                                                        "test-runner-orchestrator"
                                                        "user-input"
                                                        "validator"
                                                        "version"
                                                        "workspace"
                                                        "workspace-clj"
                                                        "ws-explorer"
                                                        "ws-file"]}}
          "migrator"                 {:src  {:direct   ["common"
                                                        "config-reader"]
                                             :indirect ["deps"
                                                        "file"
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-config"
                                                        "util"
                                                        "validator"]}
                                      :test {}}
          "path-finder"              {:src  {:direct ["file"
                                                      "util"]}
                                      :test {:direct ["file"
                                                      "util"]}}
          "poly-cli"                 {:src  {:direct   ["command"
                                                        "user-input"
                                                        "util"]
                                             :indirect ["change"
                                                        "common"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "file"
                                                        "git"
                                                        "help"
                                                        "lib"
                                                        "migrator"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "tap"
                                                        "test-runner-contract"
                                                        "test-runner-orchestrator"
                                                        "text-table"
                                                        "user-config"
                                                        "validator"
                                                        "version"
                                                        "workspace"
                                                        "workspace-clj"
                                                        "ws-explorer"
                                                        "ws-file"]}
                                      :test {:direct   ["command"
                                                        "config-reader"
                                                        "user-input"
                                                        "util"
                                                        "validator"
                                                        "workspace-clj"]
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
                                                        "sh"
                                                        "shell"
                                                        "tap"
                                                        "test-runner-contract"
                                                        "test-runner-orchestrator"
                                                        "text-table"
                                                        "user-config"
                                                        "version"
                                                        "workspace"
                                                        "ws-explorer"
                                                        "ws-file"]}}
          "sh"                       {:src  {}
                                      :test {}}
          "shell"                    {:src  {:direct   ["common"
                                                        "config-reader"
                                                        "file"
                                                        "sh"
                                                        "tap"
                                                        "user-config"
                                                        "user-input"
                                                        "util"
                                                        "version"
                                                        "ws-explorer"]
                                             :indirect ["deps"
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "validator"]}
                                      :test {:direct   ["common"
                                                        "config-reader"
                                                        "file"
                                                        "sh"
                                                        "tap"
                                                        "user-config"
                                                        "user-input"
                                                        "util"
                                                        "version"
                                                        "ws-explorer"]
                                             :indirect ["deps"
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "validator"]}}
          "tap"                      {:src  {}
                                      :test {}}
          "test-helper"              {:src  {}
                                      :test {:direct   ["command"
                                                        "file"
                                                        "user-config"
                                                        "user-input"]
                                             :indirect ["change"
                                                        "common"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "git"
                                                        "help"
                                                        "lib"
                                                        "migrator"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "tap"
                                                        "test-runner-contract"
                                                        "test-runner-orchestrator"
                                                        "text-table"
                                                        "util"
                                                        "validator"
                                                        "version"
                                                        "workspace"
                                                        "workspace-clj"
                                                        "ws-explorer"
                                                        "ws-file"]}}
          "test-runner-contract"     {:src  {:direct ["util"]}
                                      :test {:direct ["util"]}}
          "test-runner-orchestrator" {:src  {:direct   ["common"
                                                        "deps"
                                                        "test-runner-contract"
                                                        "util"
                                                        "validator"]
                                             :indirect ["file"
                                                        "path-finder"
                                                        "text-table"
                                                        "user-config"]}
                                      :test {:direct   ["common"
                                                        "deps"
                                                        "test-runner-contract"
                                                        "util"
                                                        "validator"]
                                             :indirect ["file"
                                                        "path-finder"
                                                        "text-table"
                                                        "user-config"]}}
          "text-table"               {:src  {:direct ["util"]}
                                      :test {}}
          "user-config"              {:src  {:direct ["file"
                                                      "util"]}
                                      :test {}}
          "user-input"               {:src  {:direct ["util"]}
                                      :test {:direct ["util"]}}
          "util"                     {:src  {}
                                      :test {}}
          "validator"                {:src  {:direct   ["common"
                                                        "deps"
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "util"]
                                             :indirect ["file"
                                                        "text-table"
                                                        "user-config"]}
                                      :test {:direct   ["common"
                                                        "deps"
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "util"]
                                             :indirect ["file"
                                                        "text-table"
                                                        "user-config"]}}
          "version"                  {:src  {}
                                      :test {}}
          "workspace"                {:src  {:direct   ["common"
                                                        "deps"
                                                        "file"
                                                        "path-finder"
                                                        "text-table"
                                                        "util"
                                                        "validator"]
                                             :indirect ["test-runner-contract"
                                                        "user-config"]}
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
                                                        "config-reader"
                                                        "creator"
                                                        "git"
                                                        "help"
                                                        "lib"
                                                        "migrator"
                                                        "sh"
                                                        "shell"
                                                        "tap"
                                                        "test-runner-contract"
                                                        "test-runner-orchestrator"
                                                        "user-config"
                                                        "user-input"
                                                        "version"
                                                        "workspace"
                                                        "workspace-clj"
                                                        "ws-explorer"
                                                        "ws-file"]}}
          "workspace-clj"            {:src  {:direct   ["common"
                                                        "config-reader"
                                                        "deps"
                                                        "file"
                                                        "git"
                                                        "lib"
                                                        "path-finder"
                                                        "user-config"
                                                        "util"
                                                        "validator"
                                                        "version"]
                                             :indirect ["sh"
                                                        "test-runner-contract"
                                                        "text-table"]}
                                      :test {:direct   ["common"
                                                        "config-reader"
                                                        "deps"
                                                        "file"
                                                        "git"
                                                        "lib"
                                                        "path-finder"
                                                        "user-config"
                                                        "util"
                                                        "validator"
                                                        "version"]
                                             :indirect ["sh"
                                                        "test-runner-contract"
                                                        "text-table"]}}
          "ws-explorer"              {:src  {:direct ["util"]}
                                      :test {:direct ["util"]}}
          "ws-file"                  {:src  {:direct   ["common"
                                                        "file"
                                                        "git"
                                                        "util"
                                                        "version"]
                                             :indirect ["sh"
                                                        "user-config"]}
                                      :test {}}})))

(deftest poly-project-src-paths
  (is (= (ws-explorer/extract (workspace) ["projects" "poly" "paths" "src"])
         ["bases/poly-cli/src"
          "components/change/src"
          "components/clojure-test-test-runner/src"
          "components/command/src"
          "components/common/src"
          "components/config-reader/src"
          "components/creator/resources"
          "components/creator/src"
          "components/deps/src"
          "components/file/src"
          "components/git/src"
          "components/help/src"
          "components/lib/src"
          "components/migrator/src"
          "components/path-finder/src"
          "components/sh/src"
          "components/shell/src"
          "components/tap/src"
          "components/test-runner-contract/src"
          "components/test-runner-orchestrator/src"
          "components/text-table/src"
          "components/user-config/src"
          "components/user-input/src"
          "components/util/src"
          "components/validator/src"
          "components/version/src"
          "components/workspace-clj/src"
          "components/workspace/src"
          "components/ws-explorer/src"
          "components/ws-file/src"])))

(deftest poly-project-test-paths
  (is (= (ws-explorer/extract (workspace) ["projects" "poly" "paths" "test"])
         ["bases/poly-cli/test"
          "components/change/test"
          "components/clojure-test-test-runner/test"
          "components/command/test"
          "components/common/test"
          "components/config-reader/test"
          "components/creator/test"
          "components/deps/test"
          "components/file/test"
          "components/git/test"
          "components/lib/test"
          "components/migrator/test"
          "components/path-finder/test"
          "components/shell/test"
          "components/test-helper/resources"
          "components/test-helper/src"
          "components/test-runner-contract/test"
          "components/test-runner-orchestrator/test"
          "components/user-input/test"
          "components/util/test"
          "components/validator/test"
          "components/workspace-clj/test"
          "components/workspace/test"
          "components/ws-explorer/test"
          "projects/poly/test"])))

(deftest poly-project-lib-imports
  (is (= (ws-explorer/extract (workspace) ["projects" "poly" "lib-imports"])
         {:src  ["clojure.edn"
                 "clojure.java.io"
                 "clojure.java.shell"
                 "clojure.lang"
                 "clojure.pprint"
                 "clojure.set"
                 "clojure.stacktrace"
                 "clojure.string"
                 "clojure.tools.deps"
                 "clojure.tools.deps.util.maven"
                 "clojure.tools.reader"
                 "clojure.walk"
                 "edamame.core"
                 "java.io"
                 "java.net"
                 "java.nio.file"
                 "java.util"
                 "malli.core"
                 "malli.error"
                 "malli.util"
                 "me.raynes.fs"
                 "org.eclipse.aether.util.version"
                 "org.jline.reader"
                 "org.jline.reader.impl"
                 "org.jline.terminal"
                 "portal.api"
                 "puget.printer"
                 "zprint.core"]
          :test ["clojure.java.shell"
                 "clojure.lang"
                 "clojure.string"
                 "clojure.test"
                 "malli.core"
                 "polylith.clj.core.poly-cli.api"
                 "polylith.clj.core.poly-cli.core"
                 "polylith.clj.core.test_runner_contract.interface"]})))

(deftest shell-component-lib-deps
  (is (= (ws-explorer/extract (workspace) ["components" "shell" "lib-deps"])
         {:src {"org.jline/jline" {:size    994664
                                   :type    "maven"
                                   :version "3.21.0"}}})))
