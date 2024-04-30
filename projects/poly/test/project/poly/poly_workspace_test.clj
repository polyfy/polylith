(ns project.poly.poly-workspace-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.text-table.brick-deps-table :as brick-ifc-deps]
            [polylith.clj.core.deps.text-table.brick-project-deps-table :as brick-deps-table]
            [polylith.clj.core.deps.text-table.workspace-project-deps-table :as ws-project-deps-table]
            [polylith.clj.core.deps.text-table.workspace-deps-table :as ws-ifc-deps-table]
            [polylith.clj.core.lib.text-table.lib-table :as libs]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.workspace.interface :as workspace]
            [polylith.clj.core.info.interface :as info]
            [polylith.clj.core.ws-explorer.core :as ws-explorer]))

(defn workspace [& args]
  (-> (user-input/extract-arguments (concat ["info" (str "ws-dir:.") "color-mode:none" "since:0aaeb58"] args))
      workspace/workspace))

(defn run-cmd-args [ws-dir cmd args]
  (let [input (user-input/extract-arguments (concat [cmd] args [(str "ws-dir:" ws-dir) "fake-sha:1234567" "fake-tag:" "color-mode:none"]))]
    (with-out-str
      (-> input command/execute-command))))

(defn run-cmd-plain [ws-dir cmd & args]
  (run-cmd-args ws-dir cmd args))

(defn run-cmd [ws-dir cmd & args]
  (str/split-lines (run-cmd-args ws-dir cmd args)))

(defn ws-get [ws-dir ws-param & args]
  (let [workspace (-> (user-input/extract-arguments (concat ["version" (str "ws-dir:" ws-dir) "color-mode:none"] args))
                      workspace/workspace)]
    (get-in workspace ws-param)))

(deftest ws-bricks-to-test
  (is (= ["database" "invoicer" "invoicer-cli" "test-helper" "test-helper-db" "util"]
         (read-string
           (run-cmd-plain "examples/local-dep"
                          "ws"
                          "get:projects:invoicing:bricks-to-test"
                          "changed-files:components/util/")))))

(deftest info-mark-for-testing
  (is (= ["  stable since: 1234567                     "
          "                                            "
          "  projects: 2   interfaces: 7               "
          "  bases:    1   components: 7               "
          "                                            "
          "  project        alias  status   dev        "
          "  ----------------------------   ---        "
          "  invoicing +    inv     ---     ---        "
          "  development +  dev     s--     s--        "
          "                                            "
          "  interface       brick            inv   dev"
          "  ------------------------------   ---   ---"
          "  -               without-src      -t-   -t-"
          "  database        database         stx   st-"
          "  datomic-ions    datomic-ions     s--   s--"
          "  invoicer        invoicer         stx   st-"
          "  test-helper     test-helper      -tx   st-"
          "  test-helper-db  test-helper-db   -tx   s--"
          "  util            util *           stx   st-"
          "  -               invoicer-cli     stx   st-"]
         (run-cmd "examples/local-dep"
                  "info"
                  "changed-files:components/util/"))))

(deftest polylith-project-table
  (is (= ["  project        alias  status   dev  extended   "
          "  ----------------------------   -------------   "
          "  poly *         poly    -t-     -t-     --      "
          "  polyx *        polyx   ---     ---     --      "
          "  development *  dev     s--     s--     --      "]
         (info/project-table (workspace) false false))))

(deftest polylith-info
  (is (= ["  interface                 brick                        poly  polyx   dev  extended"
          "  ----------------------------------------------------   -----------   -------------"
          "  antq                      antq *                       s--    s--    s--     --   "
          "  api                       api *                        s--    ---    s--     --   "
          "  change                    change *                     stx    s--    st-     --   "
          "  check                     check *                      s--    s--    s--     --   "
          "  clojure-test-test-runner  clojure-test-test-runner *   stx    s--    st-     --   "
          "  command                   command *                    stx    s--    st-     --   "
          "  common                    common *                     stx    s--    st-     --   "
          "  config-reader             config-reader *              stx    s--    st-     --   "
          "  creator                   creator *                    stx    s--    st-     --   "
          "  deps                      deps *                       stx    s--    st-     -t   "
          "  doc                       doc *                        s--    s--    s--     --   "
          "  file                      file *                       s--    s--    s--     --   "
          "  git                       git *                        stx    s--    st-     --   "
          "  help                      help *                       s--    s--    s--     --   "
          "  image-creator             image-creator *              s--    ---    s--     --   "
          "  image-creator             image-creator-x *            ---    s--    ---     s-   "
          "  info                      info *                       stx    s--    st-     --   "
          "  interface                 interface *                  s--    s--    s--     --   "
          "  lib                       lib *                        stx    s--    st-     --   "
          "  maven                     maven *                      s--    s--    s--     --   "
          "  overview                  overview *                   s--    s--    s--     --   "
          "  path-finder               path-finder *                stx    s--    st-     --   "
          "  sh                        sh *                         s--    s--    s--     --   "
          "  shell                     shell *                      stx    s--    st-     --   "
          "  system                    system *                     s--    ---    s--     --   "
          "  system                    system-x *                   ---    s--    ---     s-   "
          "  tap                       tap *                        s--    s--    s--     --   "
          "  test                      test *                       stx    s--    st-     --   "
          "  test-helper               test-helper *                -tx    ---    s--     --   "
          "  test-runner-contract      test-runner-contract *       stx    s--    st-     --   "
          "  test-runner-orchestrator  test-runner-orchestrator *   stx    s--    st-     --   "
          "  text-table                text-table *                 s--    s--    s--     --   "
          "  user-config               user-config *                s--    s--    s--     --   "
          "  user-input                user-input *                 stx    s--    st-     --   "
          "  util                      util *                       stx    s--    st-     --   "
          "  validator                 validator *                  stx    s--    st-     --   "
          "  version                   version *                    s--    s--    s--     --   "
          "  workspace                 workspace *                  stx    s--    st-     --   "
          "  ws-explorer               ws-explorer *                stx    s--    st-     --   "
          "  ws-file                   ws-file *                    s--    s--    s--     --   "
          "  -                         nav-generator *              s--    ---    s--     --   "
          "  -                         poly-cli *                   stx    s--    st-     --   "]
         (info/brick-table (workspace) false false))))

(defn keep-except [exclude rows]
  (filterv #(nil? (str/index-of % exclude))
           rows))

(deftest polylith-libs
  (is (= ["                                                                                                         i               "
          "                                                                                                         m               "
          "                                                                                                         a               "
          "                                                                                                         g               "
          "                                                                                                         e              w"
          "                                                                                                         -              s"
          "                                                                                                         c        v  w  -"
          "                                                                                                         r        a  o  e"
          "                                                                                                         e        l  r  x"
          "                                                                                                         a        i  k  p"
          "                                                                                                         t  s     d  s  l"
          "                                                                                                a  d  f  o  h     a  p  o"
          "                                                                                                n  e  i  r  e  t  t  a  r"
          "                                                                                                t  p  l  -  l  a  o  c  e"
          "  library                      version    type      KB   poly  polyx   dev  default  extended   q  s  e  x  l  p  r  e  r"
          "  ----------------------------------------------------   -----------   ----------------------   -------------------------"
          "  borkdude/edamame             1.4.25     maven     24    x      x      x      -        -       .  .  x  .  .  .  .  .  ."
          "  clj-commons/fs               1.6.311    maven     12    x      x      x      -        -       .  .  x  .  .  .  .  .  ."
          "  com.github.liquidz/antq      2.8.1194   maven     52    x      x      x      -        -       x  .  .  .  .  .  .  .  ."
          "  djblue/portal                0.55.1     maven  1,874    x      x      x      -        -       .  .  .  .  .  x  .  .  ."
          "  metosin/malli                0.15.0     maven     89    x      x      x      -        -       .  .  .  .  .  .  x  .  ."
          "  mvxcvi/puget                 1.3.4      maven     15    x      x      x      -        -       .  .  .  .  .  .  .  .  x"
          "  org.clojure/clojure          1.11.2     maven  4,009    x      x      x      -        -       .  .  .  .  .  .  .  .  ."
          "  org.clojure/tools.deps       0.19.1417  maven     58    x      x      x      -        -       .  x  x  .  .  .  .  x  ."
          "  org.jline/jline              3.25.1     maven  1,390    x      x      x      -        -       .  .  .  .  x  .  .  .  ."
          "  org.slf4j/slf4j-nop          2.0.13     maven      4    x      x      x      -        -       .  .  .  .  .  .  .  .  ."
          "  pjstadig/humane-test-output  0.11.0     maven      7    t      -      -      -        -       .  .  .  .  .  .  .  .  ."
          "  rewrite-clj/rewrite-clj      1.1.47     maven     73    -      -      x      -        -       .  .  .  .  .  .  .  .  ."]
         (keep-except "clojure2d"
                      (libs/table (workspace))))))

(deftest polylith-workspace-ifc-deps-table
  (is (= ["                                                                                                          t                           "
          "                                                                                                          e                           "
          "                                                                                                          s                           "
          "                                                                                                          t                           "
          "                                                                                                       t  -                           "
          "                                                                                                       e  r                           "
          "                                                                                                       s  u                           "
          "                                                                                                       t  n                           "
          "                                                                                                       -  n                           "
          "                                                                                                       r  e                           "
          "                                                                                                       u  r                           "
          "                                           c                    i                                      n  -                           "
          "                                           o                    m                                      n  o                           "
          "                                           n                    a                 p                 t  e  r     u                 w   "
          "                                           f                    g                 a                 e  r  c  t  s  u              s   "
          "                                           i                    e     i           t                 s  -  h  e  e  s     v     w  -   "
          "                                           g                    -     n        o  h                 t  c  e  x  r  e     a     o  e   "
          "                                     c     -  c                 c     t        v  -                 -  o  s  t  -  r     l  v  r  x  w"
          "                               c     o  c  r  r                 r     e        e  f        s        h  n  t  -  c  -     i  e  k  p  s"
          "                               h  c  m  o  e  e                 e     r     m  r  i     s  y        e  t  r  t  o  i     d  r  s  l  -"
          "                            a  a  h  m  m  a  a  d     f     h  a  i  f     a  v  n     h  s     t  l  r  a  a  n  n  u  a  s  p  o  f"
          "                            n  n  e  a  m  d  t  e  d  i  g  e  t  n  a  l  v  i  d     e  t  t  e  p  a  t  b  f  p  t  t  i  a  r  i"
          "                            t  g  c  n  o  e  o  p  o  l  i  l  o  f  c  i  e  e  e  s  l  e  a  s  e  c  o  l  i  u  i  o  o  c  e  l"
          "  brick                     q  e  k  d  n  r  r  s  c  e  t  p  r  o  e  b  n  w  r  h  l  m  p  t  r  t  r  e  g  t  l  r  n  e  r  e"
          "  ------------------------------------------------------------------------------------------------------------------------------------"
          "  antq                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
          "  api                       .  .  x  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  x  .  .  x  x  x  ."
          "  change                    .  .  .  .  x  .  .  .  .  .  x  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
          "  check                     .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  ."
          "  clojure-test-test-runner  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  ."
          "  command                   .  .  x  .  x  x  x  x  x  x  x  x  .  x  .  x  .  x  .  .  x  .  x  .  .  .  x  .  x  .  x  .  x  x  x  ."
          "  common                    .  .  .  .  .  .  .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  x  .  x  .  .  ."
          "  config-reader             .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  ."
          "  creator                   .  .  .  .  x  .  .  .  .  x  x  .  .  .  .  .  .  .  .  .  .  .  .  .  t  .  .  .  .  .  x  .  x  .  .  ."
          "  deps                      .  .  .  .  x  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  x  .  .  .  .  ."
          "  doc                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  ."
          "  file                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
          "  git                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
          "  help                      .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  x  .  x  .  .  ."
          "  image-creator             .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  image-creator-x           .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
          "  info                      .  .  .  .  x  .  .  .  .  t  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  x  .  .  x  x  .  .  .  ."
          "  interface                 .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  lib                       x  .  .  .  x  x  .  .  .  x  .  .  .  .  .  .  x  .  .  .  .  .  .  .  t  .  .  x  x  .  x  .  .  .  .  ."
          "  maven                     .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  overview                  .  .  .  .  x  .  .  x  .  .  .  .  x  x  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  ."
          "  path-finder               .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
          "  sh                        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  shell                     x  .  .  .  x  .  .  .  x  x  x  .  .  .  .  x  .  .  .  x  .  x  x  .  .  .  .  .  x  x  x  .  .  x  x  ."
          "  system                    .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  system-x                  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  tap                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  test                      .  .  .  .  x  .  .  .  .  .  x  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  test-helper               .  .  .  x  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  .  ."
          "  test-runner-contract      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
          "  test-runner-orchestrator  .  .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  x  .  .  .  ."
          "  text-table                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
          "  user-config               .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
          "  user-input                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
          "  util                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  validator                 .  .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  ."
          "  version                   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  workspace                 x  x  .  .  x  x  .  x  .  x  x  .  .  .  x  x  .  .  x  .  .  .  .  x  .  .  .  .  x  x  x  x  x  .  .  x"
          "  ws-explorer               .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
          "  ws-file                   .  .  .  .  x  .  .  .  .  x  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  x  .  .  ."
          "  nav-generator             .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
          "  poly-cli                  .  .  .  x  .  t  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  t  .  t  .  ."]
         (ws-ifc-deps-table/table (workspace)))))

(deftest polylith-workspace-project-deps-table
  (let [ws (workspace)
        projects (:projects ws)
        project (common/find-project "poly" projects)]
    (is (= ["                                                                                                          t                           "
            "                                                                                                          e                           "
            "                                                                                                          s                           "
            "                                                                                                          t                           "
            "                                                                                                       t  -                           "
            "                                                                                                       e  r                           "
            "                                                                                                       s  u                           "
            "                                                                                                       t  n                           "
            "                                                                                                       -  n                           "
            "                                                                                                       r  e                           "
            "                                                                                                       u  r                           "
            "                                           c                    i                                      n  -                           "
            "                                           o                    m                                      n  o                           "
            "                                           n                    a                 p                 t  e  r     u                 w   "
            "                                           f                    g                 a                 e  r  c  t  s  u              s   "
            "                                           i                    e     i           t                 s  -  h  e  e  s     v     w  -   "
            "                                           g                    -     n        o  h                 t  c  e  x  r  e     a     o  e   "
            "                                     c     -  c                 c     t        v  -                 -  o  s  t  -  r     l  v  r  x  w"
            "                               c     o  c  r  r                 r     e        e  f        s        h  n  t  -  c  -     i  e  k  p  s"
            "                               h  c  m  o  e  e                 e     r     m  r  i     s  y        e  t  r  t  o  i     d  r  s  l  -"
            "                            a  a  h  m  m  a  a  d     f     h  a  i  f     a  v  n     h  s     t  l  r  a  a  n  n  u  a  s  p  o  f"
            "                            n  n  e  a  m  d  t  e  d  i  g  e  t  n  a  l  v  i  d     e  t  t  e  p  a  t  b  f  p  t  t  i  a  r  i"
            "                            t  g  c  n  o  e  o  p  o  l  i  l  o  f  c  i  e  e  e  s  l  e  a  s  e  c  o  l  i  u  i  o  o  c  e  l"
            "  brick                     q  e  k  d  n  r  r  s  c  e  t  p  r  o  e  b  n  w  r  h  l  m  p  t  r  t  r  e  g  t  l  r  n  e  r  e"
            "  ------------------------------------------------------------------------------------------------------------------------------------"
            "  antq                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  api                       +  +  x  .  x  +  .  +  .  +  +  .  +  .  +  +  +  .  +  +  .  +  .  +  .  +  x  +  +  x  +  +  x  x  x  +"
            "  change                    .  .  .  .  x  .  .  .  .  +  x  .  +  .  .  .  .  .  x  +  .  +  .  .  .  .  .  +  +  .  x  .  +  .  .  ."
            "  check                     .  .  .  .  +  .  .  +  .  +  .  .  +  .  +  .  .  .  +  .  .  +  .  .  .  +  .  +  +  .  x  x  +  .  .  ."
            "  clojure-test-test-runner  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  ."
            "  command                   +  +  x  .  x  x  x  x  x  x  x  x  +  x  +  x  +  x  +  +  x  +  x  +  .  +  x  +  x  +  x  +  x  x  x  +"
            "  common                    .  .  .  .  .  .  .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  +  .  .  .  .  .  x  x  .  x  .  x  .  .  ."
            "  config-reader             .  .  .  .  x  .  .  +  .  x  .  .  +  .  +  .  .  .  +  .  .  +  .  .  .  +  .  +  +  .  x  x  +  .  .  ."
            "  creator                   -  -  -  -  x  -  -  -  -  x  x  -  +  -  -  -  -  -  -  +  -  +  -  -  t  -  -  +  +  -  x  -  x  -  -  -"
            "  deps                      .  .  .  .  x  .  .  .  .  +  .  .  +  .  x  .  .  .  .  .  .  +  .  .  .  .  .  x  x  .  x  .  +  .  .  ."
            "  doc                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  +  .  .  .  .  .  .  .  .  .  .  x  .  .  ."
            "  file                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  git                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  help                      .  .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  .  .  .  x  .  .  .  .  .  +  +  .  x  .  x  .  .  ."
            "  image-creator             .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  info                      .  .  .  .  x  .  .  +  .  t  .  .  +  .  +  .  .  .  x  .  .  +  .  .  .  +  .  x  +  .  x  x  +  .  .  ."
            "  interface                 .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  lib                       x  -  -  -  x  x  -  +  -  x  -  -  +  -  +  -  x  -  +  -  -  +  -  -  t  +  -  x  x  -  x  +  +  -  -  -"
            "  maven                     .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  overview                  +  .  .  .  x  +  .  x  .  +  .  .  x  x  +  x  +  .  +  .  .  +  .  .  .  +  .  +  +  x  x  +  +  .  .  ."
            "  path-finder               .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  sh                        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  shell                     x  +  .  .  x  +  .  +  x  x  x  .  +  .  +  x  +  .  +  x  .  x  x  +  .  +  .  +  x  x  x  +  +  x  x  +"
            "  system                    .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  tap                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  test                      .  .  .  .  x  .  .  .  .  +  x  .  +  .  .  .  .  .  x  +  .  +  .  .  .  .  .  +  +  .  +  .  +  .  .  ."
            "  test-helper               -  -  -  t  -  -  -  -  -  t  -  -  -  -  -  -  -  -  -  -  -  -  -  -  .  -  -  -  t  t  -  -  -  -  -  -"
            "  test-runner-contract      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  test-runner-orchestrator  .  .  .  .  x  .  .  x  .  +  .  .  +  .  +  .  .  .  +  .  .  +  .  .  .  x  .  +  +  .  x  x  +  .  .  ."
            "  text-table                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  user-config               .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  user-input                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  util                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  validator                 .  .  .  .  x  .  .  x  .  +  .  .  +  .  +  .  .  .  x  .  .  +  .  .  .  x  .  +  +  .  x  .  +  .  .  ."
            "  version                   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  workspace                 x  x  .  .  x  x  .  x  .  x  x  .  +  .  x  x  +  .  x  +  .  +  .  x  .  +  .  +  x  x  x  x  x  .  .  x"
            "  ws-explorer               .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  ws-file                   .  .  .  .  x  .  .  .  .  x  x  .  +  .  .  .  .  .  .  +  .  +  .  .  .  .  .  +  +  .  x  .  x  .  .  ."
            "  nav-generator             .  .  .  .  +  x  .  +  .  +  .  .  +  .  +  .  .  .  +  .  .  +  .  .  .  +  .  +  +  .  x  +  +  .  .  ."
            "  poly-cli                  +  +  +  x  +  t  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  .  +  +  +  +  x  x  t  +  t  +  +"]
           (ws-project-deps-table/table ws project)))))

(deftest polylith-workspace-project-deps-table-indirect
  (let [ws (workspace)
        projects (:projects ws)
        project (common/find-project "poly" projects)]
    (is (= ["                                                                                                          t                           "
            "                                                                                                          e                           "
            "                                                                                                          s                           "
            "                                                                                                          t                           "
            "                                                                                                       t  -                           "
            "                                                                                                       e  r                           "
            "                                                                                                       s  u                           "
            "                                                                                                       t  n                           "
            "                                                                                                       -  n                           "
            "                                                                                                       r  e                           "
            "                                                                                                       u  r                           "
            "                                           c                    i                                      n  -                           "
            "                                           o                    m                                      n  o                           "
            "                                           n                    a                 p                 t  e  r     u                 w   "
            "                                           f                    g                 a                 e  r  c  t  s  u              s   "
            "                                           i                    e     i           t                 s  -  h  e  e  s     v     w  -   "
            "                                           g                    -     n        o  h                 t  c  e  x  r  e     a     o  e   "
            "                                     c     -  c                 c     t        v  -                 -  o  s  t  -  r     l  v  r  x  w"
            "                               c     o  c  r  r                 r     e        e  f        s        h  n  t  -  c  -     i  e  k  p  s"
            "                               h  c  m  o  e  e                 e     r     m  r  i     s  y        e  t  r  t  o  i     d  r  s  l  -"
            "                            a  a  h  m  m  a  a  d     f     h  a  i  f     a  v  n     h  s     t  l  r  a  a  n  n  u  a  s  p  o  f"
            "                            n  n  e  a  m  d  t  e  d  i  g  e  t  n  a  l  v  i  d     e  t  t  e  p  a  t  b  f  p  t  t  i  a  r  i"
            "                            t  g  c  n  o  e  o  p  o  l  i  l  o  f  c  i  e  e  e  s  l  e  a  s  e  c  o  l  i  u  i  o  o  c  e  l"
            "  brick                     q  e  k  d  n  r  r  s  c  e  t  p  r  o  e  b  n  w  r  h  l  m  p  t  r  t  r  e  g  t  l  r  n  e  r  e"
            "  ------------------------------------------------------------------------------------------------------------------------------------"
            "  antq                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  api                       +  +  x  .  x  +  .  +  .  +  +  .  +  .  +  +  +  .  +  +  .  +  .  +  .  +  x  +  +  x  +  +  x  x  x  +"
            "  change                    .  .  .  .  x  .  .  .  .  +  x  .  +  .  .  .  .  .  x  +  .  +  .  .  .  .  .  +  +  .  x  .  +  .  .  ."
            "  check                     .  .  .  .  +  .  .  +  .  +  .  .  +  .  +  .  .  .  +  .  .  +  .  .  .  +  .  +  +  .  x  x  +  .  .  ."
            "  clojure-test-test-runner  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  ."
            "  command                   +  +  x  .  x  x  x  x  x  x  x  x  +  x  +  x  +  x  +  +  x  +  x  +  .  +  x  +  x  +  x  +  x  x  x  +"
            "  common                    .  .  .  .  .  .  .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  +  .  .  .  .  .  x  x  .  x  .  x  .  .  ."
            "  config-reader             .  .  .  .  x  .  .  +  .  x  .  .  +  .  +  .  .  .  +  .  .  +  .  .  .  +  .  +  +  .  x  x  +  .  .  ."
            "  creator                   -  -  -  -  x  -  -  -  -  x  x  -  +  -  -  -  -  -  -  +  -  +  -  -  t  -  -  +  +  -  x  -  x  -  -  -"
            "  deps                      .  .  .  .  x  .  .  .  .  +  .  .  +  .  x  .  .  .  .  .  .  +  .  .  .  .  .  x  x  .  x  .  +  .  .  ."
            "  doc                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  +  .  .  .  .  .  .  .  .  .  .  x  .  .  ."
            "  file                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  git                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  help                      .  .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  .  .  .  x  .  .  .  .  .  +  +  .  x  .  x  .  .  ."
            "  image-creator             .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  info                      .  .  .  .  x  .  .  +  .  t  .  .  +  .  +  .  .  .  x  .  .  +  .  .  .  +  .  x  +  .  x  x  +  .  .  ."
            "  interface                 .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  lib                       x  -  -  -  x  x  -  +  -  x  -  -  +  -  +  -  x  -  +  -  -  +  -  -  t  +  -  x  x  -  x  +  +  -  -  -"
            "  maven                     .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  overview                  +  .  .  .  x  +  .  x  .  +  .  .  x  x  +  x  +  .  +  .  .  +  .  .  .  +  .  +  +  x  x  +  +  .  .  ."
            "  path-finder               .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  sh                        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  shell                     x  +  .  .  x  +  .  +  x  x  x  .  +  .  +  x  +  .  +  x  .  x  x  +  .  +  .  +  x  x  x  +  +  x  x  +"
            "  system                    .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  tap                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  test                      .  .  .  .  x  .  .  .  .  +  x  .  +  .  .  .  .  .  x  +  .  +  .  .  .  .  .  +  +  .  +  .  +  .  .  ."
            "  test-helper               -  -  -  t  -  -  -  -  -  t  -  -  -  -  -  -  -  -  -  -  -  -  -  -  .  -  -  -  t  t  -  -  -  -  -  -"
            "  test-runner-contract      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  test-runner-orchestrator  .  .  .  .  x  .  .  x  .  +  .  .  +  .  +  .  .  .  +  .  .  +  .  .  .  x  .  +  +  .  x  x  +  .  .  ."
            "  text-table                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  user-config               .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  user-input                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  util                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  validator                 .  .  .  .  x  .  .  x  .  +  .  .  +  .  +  .  .  .  x  .  .  +  .  .  .  x  .  +  +  .  x  .  +  .  .  ."
            "  version                   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  workspace                 x  x  .  .  x  x  .  x  .  x  x  .  +  .  x  x  +  .  x  +  .  +  .  x  .  +  .  +  x  x  x  x  x  .  .  x"
            "  ws-explorer               .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  ."
            "  ws-file                   .  .  .  .  x  .  .  .  .  x  x  .  +  .  .  .  .  .  .  +  .  +  .  .  .  .  .  +  +  .  x  .  x  .  .  ."
            "  nav-generator             .  .  .  .  +  x  .  +  .  +  .  .  +  .  +  .  .  .  +  .  .  +  .  .  .  +  .  +  +  .  x  +  +  .  .  ."
            "  poly-cli                  +  +  +  x  +  t  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  .  +  +  +  +  x  x  t  +  t  +  +"]
           (ws-project-deps-table/table ws project)))))

(deftest polylith-brick-and-project-deps
  (let [{:keys [components projects] :as ws} (workspace)
        project (common/find-project "poly" projects)
        brick (common/find-component "workspace" components)]
    (is (= ["  used by       <  workspace  >  uses         "
            "  ------------                   -------------"
            "  api                            antq         "
            "  command                        change       "
            "  shell                          common       "
            "  poly-cli (t)                   config-reader"
            "                                 deps         "
            "                                 file         "
            "                                 git          "
            "                                 interface    "
            "                                 lib          "
            "                                 path-finder  "
            "                                 test         "
            "                                 user-config  "
            "                                 user-input   "
            "                                 util         "
            "                                 validator    "
            "                                 version      "
            "                                 ws-file      "]
           (brick-deps-table/table ws project brick "none")))))

(deftest polylith-project-brick-deps
  (let [{:keys [components] :as ws} (workspace)
        brick (common/find-component "workspace" components)]
    (is (= ["  used by       <  workspace  >  uses         "
            "  ------------                   -------------"
            "  api                            antq         "
            "  command                        change       "
            "  shell                          common       "
            "  poly-cli (t)                   config-reader"
            "                                 deps         "
            "                                 file         "
            "                                 git          "
            "                                 interface    "
            "                                 lib          "
            "                                 path-finder  "
            "                                 test         "
            "                                 user-config  "
            "                                 user-input   "
            "                                 util         "
            "                                 validator    "
            "                                 version      "
            "                                 ws-file      "]
           (brick-ifc-deps/table ws brick)))))

(deftest polylith-poly-project-deps
  (is (= {"antq"                     {:src  {:direct ["maven"
                                                      "util"]}
                                      :test {}}
          "api"                      {:src  {:direct   ["check"
                                                        "common"
                                                        "test-runner-orchestrator"
                                                        "user-input"
                                                        "version"
                                                        "workspace"
                                                        "ws-explorer"]
                                             :indirect ["antq"
                                                        "change"
                                                        "config-reader"
                                                        "deps"
                                                        "file"
                                                        "git"
                                                        "image-creator"
                                                        "interface"
                                                        "lib"
                                                        "maven"
                                                        "path-finder"
                                                        "sh"
                                                        "system"
                                                        "test"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-config"
                                                        "util"
                                                        "validator"
                                                        "ws-file"]}
                                      :test {}}
          "change"                   {:src  {:direct   ["common"
                                                        "git"
                                                        "path-finder"
                                                        "util"]
                                             :indirect ["file"
                                                        "image-creator"
                                                        "sh"
                                                        "system"
                                                        "text-table"
                                                        "user-config"
                                                        "version"]}
                                      :test {:direct   ["common"
                                                        "git"
                                                        "path-finder"
                                                        "util"]
                                             :indirect ["file"
                                                        "image-creator"
                                                        "sh"
                                                        "system"
                                                        "text-table"
                                                        "user-config"
                                                        "version"]}}
          "check"                    {:src  {:direct   ["util"
                                                        "validator"]
                                             :indirect ["common"
                                                        "deps"
                                                        "file"
                                                        "image-creator"
                                                        "interface"
                                                        "path-finder"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-config"
                                                        "version"]}
                                      :test {}}
          "clojure-test-test-runner" {:src  {:direct ["test-runner-contract"
                                                      "util"]}
                                      :test {:direct ["test-runner-contract"
                                                      "util"]}}
          "command"                  {:src  {:direct   ["check"
                                                        "common"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "file"
                                                        "git"
                                                        "help"
                                                        "info"
                                                        "lib"
                                                        "overview"
                                                        "shell"
                                                        "tap"
                                                        "test-runner-orchestrator"
                                                        "user-config"
                                                        "util"
                                                        "version"
                                                        "workspace"
                                                        "ws-explorer"]
                                             :indirect ["antq"
                                                        "change"
                                                        "image-creator"
                                                        "interface"
                                                        "maven"
                                                        "path-finder"
                                                        "sh"
                                                        "system"
                                                        "test"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-input"
                                                        "validator"
                                                        "ws-file"]}
                                      :test {:direct   ["check"
                                                        "common"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "file"
                                                        "git"
                                                        "help"
                                                        "info"
                                                        "lib"
                                                        "overview"
                                                        "shell"
                                                        "tap"
                                                        "test-runner-orchestrator"
                                                        "user-config"
                                                        "util"
                                                        "version"
                                                        "workspace"
                                                        "ws-explorer"]
                                             :indirect ["antq"
                                                        "change"
                                                        "image-creator"
                                                        "interface"
                                                        "maven"
                                                        "path-finder"
                                                        "sh"
                                                        "system"
                                                        "test"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-input"
                                                        "validator"
                                                        "ws-file"]}}
          "common"                   {:src  {:direct   ["file"
                                                        "image-creator"
                                                        "text-table"
                                                        "user-config"
                                                        "util"
                                                        "version"]
                                             :indirect ["system"]}
                                      :test {:direct   ["file"
                                                        "image-creator"
                                                        "text-table"
                                                        "user-config"
                                                        "util"
                                                        "version"]
                                             :indirect ["system"]}}
          "config-reader"            {:src  {:direct   ["common"
                                                        "file"
                                                        "util"
                                                        "validator"]
                                             :indirect ["deps"
                                                        "image-creator"
                                                        "interface"
                                                        "path-finder"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-config"
                                                        "version"]}
                                      :test {:direct   ["common"
                                                        "file"
                                                        "util"
                                                        "validator"]
                                             :indirect ["deps"
                                                        "image-creator"
                                                        "interface"
                                                        "path-finder"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-config"
                                                        "version"]}}
          "creator"                  {:src  {:direct   ["common"
                                                        "file"
                                                        "git"
                                                        "util"
                                                        "version"]
                                             :indirect ["image-creator"
                                                        "sh"
                                                        "system"
                                                        "text-table"
                                                        "user-config"]}
                                      :test {:direct   ["common"
                                                        "file"
                                                        "git"
                                                        "test-helper"
                                                        "util"
                                                        "version"]
                                             :indirect ["antq"
                                                        "change"
                                                        "check"
                                                        "command"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "help"
                                                        "image-creator"
                                                        "info"
                                                        "interface"
                                                        "lib"
                                                        "maven"
                                                        "overview"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "system"
                                                        "tap"
                                                        "test"
                                                        "test-runner-contract"
                                                        "test-runner-orchestrator"
                                                        "text-table"
                                                        "user-config"
                                                        "user-input"
                                                        "validator"
                                                        "workspace"
                                                        "ws-explorer"
                                                        "ws-file"]}}
          "deps"                     {:src  {:direct   ["common"
                                                        "interface"
                                                        "text-table"
                                                        "user-config"
                                                        "util"]
                                             :indirect ["file"
                                                        "image-creator"
                                                        "system"
                                                        "version"]}
                                      :test {:direct   ["common"
                                                        "interface"
                                                        "text-table"
                                                        "user-config"
                                                        "util"]
                                             :indirect ["file"
                                                        "image-creator"
                                                        "system"
                                                        "version"]}}
          "doc"                      {:src  {:direct   ["version"]
                                             :indirect ["system"]}
                                      :test {}}
          "file"                     {:src  {:direct ["util"]}
                                      :test {}}
          "git"                      {:src  {:direct ["sh"
                                                      "util"]}
                                      :test {:direct ["sh"
                                                      "util"]}}
          "help"                     {:src  {:direct   ["common"
                                                        "system"
                                                        "util"
                                                        "version"]
                                             :indirect ["file"
                                                        "image-creator"
                                                        "text-table"
                                                        "user-config"]}
                                      :test {}}
          "image-creator"            {:src  {:direct ["file"
                                                      "util"]}
                                      :test {}}
          "info"                     {:src  {:direct   ["common"
                                                        "path-finder"
                                                        "text-table"
                                                        "util"
                                                        "validator"]
                                             :indirect ["deps"
                                                        "file"
                                                        "image-creator"
                                                        "interface"
                                                        "system"
                                                        "test-runner-contract"
                                                        "user-config"
                                                        "version"]}
                                      :test {:direct   ["common"
                                                        "file"
                                                        "path-finder"
                                                        "text-table"
                                                        "util"
                                                        "validator"]
                                             :indirect ["deps"
                                                        "image-creator"
                                                        "interface"
                                                        "system"
                                                        "test-runner-contract"
                                                        "user-config"
                                                        "version"]}}
          "interface"                {:src  {}
                                      :test {}}
          "lib"                      {:src  {:direct   ["antq"
                                                        "common"
                                                        "config-reader"
                                                        "file"
                                                        "maven"
                                                        "text-table"
                                                        "user-config"
                                                        "util"]
                                             :indirect ["deps"
                                                        "image-creator"
                                                        "interface"
                                                        "path-finder"
                                                        "system"
                                                        "test-runner-contract"
                                                        "validator"
                                                        "version"]}
                                      :test {:direct   ["antq"
                                                        "common"
                                                        "config-reader"
                                                        "file"
                                                        "maven"
                                                        "test-helper"
                                                        "text-table"
                                                        "user-config"
                                                        "util"]
                                             :indirect ["change"
                                                        "check"
                                                        "command"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "git"
                                                        "help"
                                                        "image-creator"
                                                        "info"
                                                        "interface"
                                                        "lib"
                                                        "overview"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "system"
                                                        "tap"
                                                        "test"
                                                        "test-runner-contract"
                                                        "test-runner-orchestrator"
                                                        "user-input"
                                                        "validator"
                                                        "version"
                                                        "workspace"
                                                        "ws-explorer"
                                                        "ws-file"]}}
          "maven"                    {:src  {}
                                      :test {}}
          "nav-generator"            {:src  {:direct   ["config-reader"
                                                        "util"]
                                             :indirect ["common"
                                                        "deps"
                                                        "file"
                                                        "image-creator"
                                                        "interface"
                                                        "path-finder"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-config"
                                                        "validator"
                                                        "version"]}
                                      :test {}}
          "overview"                 {:src  {:direct   ["common"
                                                        "deps"
                                                        "image-creator"
                                                        "info"
                                                        "lib"
                                                        "user-input"
                                                        "util"]
                                             :indirect ["antq"
                                                        "config-reader"
                                                        "file"
                                                        "interface"
                                                        "maven"
                                                        "path-finder"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-config"
                                                        "validator"
                                                        "version"]}
                                      :test {}}
          "path-finder"              {:src  {:direct ["file"
                                                      "util"]}
                                      :test {:direct ["file"
                                                      "util"]}}
          "poly-cli"                 {:src  {:direct   ["command"
                                                        "user-input"
                                                        "util"]
                                             :indirect ["antq"
                                                        "change"
                                                        "check"
                                                        "common"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "file"
                                                        "git"
                                                        "help"
                                                        "image-creator"
                                                        "info"
                                                        "interface"
                                                        "lib"
                                                        "maven"
                                                        "overview"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "system"
                                                        "tap"
                                                        "test"
                                                        "test-runner-contract"
                                                        "test-runner-orchestrator"
                                                        "text-table"
                                                        "user-config"
                                                        "validator"
                                                        "version"
                                                        "workspace"
                                                        "ws-explorer"
                                                        "ws-file"]}
                                      :test {:direct   ["command"
                                                        "config-reader"
                                                        "user-input"
                                                        "util"
                                                        "validator"
                                                        "workspace"]
                                             :indirect ["antq"
                                                        "change"
                                                        "check"
                                                        "common"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "file"
                                                        "git"
                                                        "help"
                                                        "image-creator"
                                                        "info"
                                                        "interface"
                                                        "lib"
                                                        "maven"
                                                        "overview"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "system"
                                                        "tap"
                                                        "test"
                                                        "test-runner-contract"
                                                        "test-runner-orchestrator"
                                                        "text-table"
                                                        "user-config"
                                                        "version"
                                                        "ws-explorer"
                                                        "ws-file"]}}
          "sh"                       {:src  {}
                                      :test {}}
          "shell"                    {:src  {:direct   ["antq"
                                                        "common"
                                                        "doc"
                                                        "file"
                                                        "git"
                                                        "lib"
                                                        "sh"
                                                        "system"
                                                        "tap"
                                                        "user-config"
                                                        "user-input"
                                                        "util"
                                                        "workspace"
                                                        "ws-explorer"]
                                             :indirect ["change"
                                                        "config-reader"
                                                        "deps"
                                                        "image-creator"
                                                        "interface"
                                                        "maven"
                                                        "path-finder"
                                                        "test"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "validator"
                                                        "version"
                                                        "ws-file"]}
                                      :test {:direct   ["antq"
                                                        "common"
                                                        "doc"
                                                        "file"
                                                        "git"
                                                        "lib"
                                                        "sh"
                                                        "system"
                                                        "tap"
                                                        "user-config"
                                                        "user-input"
                                                        "util"
                                                        "workspace"
                                                        "ws-explorer"]
                                             :indirect ["change"
                                                        "config-reader"
                                                        "deps"
                                                        "image-creator"
                                                        "interface"
                                                        "maven"
                                                        "path-finder"
                                                        "test"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "validator"
                                                        "version"
                                                        "ws-file"]}}
          "system"                   {:src  {}
                                      :test {}}
          "tap"                      {:src  {}
                                      :test {}}
          "test"                     {:src  {:direct   ["common"
                                                        "git"
                                                        "path-finder"]
                                             :indirect ["file"
                                                        "image-creator"
                                                        "sh"
                                                        "system"
                                                        "text-table"
                                                        "user-config"
                                                        "util"
                                                        "version"]}
                                      :test {:direct   ["common"
                                                        "git"
                                                        "path-finder"]
                                             :indirect ["file"
                                                        "image-creator"
                                                        "sh"
                                                        "system"
                                                        "text-table"
                                                        "user-config"
                                                        "util"
                                                        "version"]}}
          "test-helper"              {:src  {}
                                      :test {:direct   ["command"
                                                        "file"
                                                        "user-config"
                                                        "user-input"]
                                             :indirect ["antq"
                                                        "change"
                                                        "check"
                                                        "common"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "git"
                                                        "help"
                                                        "image-creator"
                                                        "info"
                                                        "interface"
                                                        "lib"
                                                        "maven"
                                                        "overview"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "system"
                                                        "tap"
                                                        "test"
                                                        "test-runner-contract"
                                                        "test-runner-orchestrator"
                                                        "text-table"
                                                        "util"
                                                        "validator"
                                                        "version"
                                                        "workspace"
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
                                                        "image-creator"
                                                        "interface"
                                                        "path-finder"
                                                        "system"
                                                        "text-table"
                                                        "user-config"
                                                        "version"]}
                                      :test {:direct   ["common"
                                                        "deps"
                                                        "test-runner-contract"
                                                        "util"
                                                        "validator"]
                                             :indirect ["file"
                                                        "image-creator"
                                                        "interface"
                                                        "path-finder"
                                                        "system"
                                                        "text-table"
                                                        "user-config"
                                                        "version"]}}
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
                                                        "image-creator"
                                                        "interface"
                                                        "system"
                                                        "text-table"
                                                        "user-config"
                                                        "version"]}
                                      :test {:direct   ["common"
                                                        "deps"
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "util"]
                                             :indirect ["file"
                                                        "image-creator"
                                                        "interface"
                                                        "system"
                                                        "text-table"
                                                        "user-config"
                                                        "version"]}}
          "version"                  {:src  {:direct ["system"]}
                                      :test {}}
          "workspace"                {:src  {:direct   ["antq"
                                                        "change"
                                                        "common"
                                                        "config-reader"
                                                        "deps"
                                                        "file"
                                                        "git"
                                                        "interface"
                                                        "lib"
                                                        "path-finder"
                                                        "test"
                                                        "user-config"
                                                        "user-input"
                                                        "util"
                                                        "validator"
                                                        "version"
                                                        "ws-file"]
                                             :indirect ["image-creator"
                                                        "maven"
                                                        "sh"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"]}
                                      :test {:direct   ["antq"
                                                        "change"
                                                        "common"
                                                        "config-reader"
                                                        "deps"
                                                        "file"
                                                        "git"
                                                        "interface"
                                                        "lib"
                                                        "path-finder"
                                                        "test"
                                                        "user-config"
                                                        "user-input"
                                                        "util"
                                                        "validator"
                                                        "version"
                                                        "ws-file"]
                                             :indirect ["image-creator"
                                                        "maven"
                                                        "sh"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"]}}
          "ws-explorer"              {:src  {:direct ["util"]}
                                      :test {:direct ["util"]}}
          "ws-file"                  {:src  {:direct   ["common"
                                                        "file"
                                                        "git"
                                                        "util"
                                                        "version"]
                                             :indirect ["image-creator"
                                                        "sh"
                                                        "system"
                                                        "text-table"
                                                        "user-config"]}
                                      :test {}}}
         (ws-explorer/extract (workspace) ["projects" "poly" "deps"]))))

(deftest polylith-poly-project-src-paths
  (is (= ["bases/nav-generator/src"
          "bases/poly-cli/src"
          "components/antq/src"
          "components/api/src"
          "components/change/src"
          "components/check/src"
          "components/clojure-test-test-runner/src"
          "components/command/src"
          "components/common/src"
          "components/config-reader/src"
          "components/creator/resources"
          "components/creator/src"
          "components/deps/src"
          "components/doc/src"
          "components/file/src"
          "components/git/src"
          "components/help/src"
          "components/image-creator/src"
          "components/info/src"
          "components/interface/src"
          "components/lib/src"
          "components/maven/src"
          "components/overview/src"
          "components/path-finder/src"
          "components/sh/src"
          "components/shell/src"
          "components/system/src"
          "components/tap/src"
          "components/test-runner-contract/src"
          "components/test-runner-orchestrator/src"
          "components/test/src"
          "components/text-table/src"
          "components/user-config/src"
          "components/user-input/src"
          "components/util/src"
          "components/validator/src"
          "components/version/src"
          "components/workspace/src"
          "components/ws-explorer/src"
          "components/ws-file/src"]
         (ws-explorer/extract (workspace) ["projects" "poly" "paths" "src"]))))

(deftest polylith-poly-project-test-paths
  (is (= ["bases/poly-cli/test"
          "components/change/test"
          "components/clojure-test-test-runner/test"
          "components/command/test"
          "components/common/test"
          "components/config-reader/test"
          "components/creator/test"
          "components/deps/test"
          "components/git/test"
          "components/info/test"
          "components/lib/test"
          "components/path-finder/test"
          "components/shell/test"
          "components/test-helper/resources"
          "components/test-helper/src"
          "components/test-runner-contract/test"
          "components/test-runner-orchestrator/test"
          "components/test/test"
          "components/user-input/test"
          "components/util/test"
          "components/validator/test"
          "components/workspace/test"
          "components/ws-explorer/test"
          "projects/poly/test"]
         (ws-explorer/extract (workspace) ["projects" "poly" "paths" "test"]))))

(deftest polylith-poly-project-lib-imports
  (is (= {:src  ["antq.api"
                 "clojure.edn"
                 "clojure.java.browse"
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
                 "org.jline.reader"
                 "org.jline.reader.impl"
                 "org.jline.terminal"
                 "polylith.clj.core.nav-generator.help-generator"
                 "polylith.clj.core.nav-generator.pages-generator"
                 "polylith.clj.core.nav-generator.ws-generator"
                 "portal.api"
                 "puget.printer"]
          :test ["clojure.lang"
                 "clojure.test"
                 "malli.core"
                 "polylith.clj.core.poly-cli.core"]}
         (ws-explorer/extract (workspace) ["projects" "poly" "lib-imports"]))))

(deftest polylith-shell-component-lib-deps
  (is (= {:src {"org.jline/jline" {:size    1423747
                                   :type    "maven"
                                   :version "3.25.1"}}}
         (ws-explorer/extract (workspace) ["components" "shell" "lib-deps"]))))

(deftest profile-info
  (is (= ["  stable since: 1234567                       "
          "                                              "
          "  projects: 2   interfaces: 5                 "
          "  bases:    2   components: 6                 "
          "                                              "
          "  active profiles: default                    "
          "                                              "
          "  project      alias  status   dev  extra     "
          "  --------------------------   ----------     "
          "  service      s       ---     ---   --       "
          "  development  dev     s--     s--   --       "
          "                                              "
          "  interface    brick           s    dev  extra"
          "  -------------------------   ---   ----------"
          "  calculator   calculator1    s--   s--   --  "
          "  database     database1      st-   st-   --  "
          "  test-helper  test-helper1   -t-   -t-   --  "
          "  user         admin          ---   ---   st  "
          "  user         user1          st-   st-   --  "
          "  util         util1          st-   st-   --  "
          "  -            base1          st-   st-   --  "
          "  -            base2          st-   st-   --  "]
         (run-cmd "examples/profiles"
                  "info"
                  ":no-changes"))))

(deftest profile-info-where-test-has-changed
  (is (= ["  stable since: 1234567                       "
          "                                              "
          "  projects: 2   interfaces: 5                 "
          "  bases:    2   components: 6                 "
          "                                              "
          "  active profiles: default                    "
          "                                              "
          "  project        alias  status   dev  extra   "
          "  ----------------------------   ----------   "
          "  service +      s       ---     ---   --     "
          "  development +  dev     s--     s--   --     "
          "                                              "
          "  interface    brick           s    dev  extra"
          "  -------------------------   ---   ----------"
          "  calculator   calculator1    s--   s--   --  "
          "  database     database1      st-   st-   --  "
          "  test-helper  test-helper1   -t-   -t-   --  "
          "  user         admin          ---   ---   st  "
          "  user         user1          st-   st-   --  "
          "  util         util1          st-   st-   --  "
          "  -            base1          stx   st-   --  "
          "  -            base2 *        stx   st-   --  "]
         (run-cmd "examples/profiles"
                  "info"
                  "changed-files:bases/base2/test/se/example/base2/core_test.clj"))))

(deftest profile-info-where-src-has-changed
  (is (= ["  stable since: 1234567                       "
          "                                              "
          "  projects: 2   interfaces: 5                 "
          "  bases:    2   components: 6                 "
          "                                              "
          "  active profiles: default                    "
          "                                              "
          "  project        alias  status   dev  extra   "
          "  ----------------------------   ----------   "
          "  service +      s       ---     ---   --     "
          "  development +  dev     s--     s--   --     "
          "                                              "
          "  interface    brick           s    dev  extra"
          "  -------------------------   ---   ----------"
          "  calculator   calculator1    s--   s--   --  "
          "  database     database1      st-   st-   --  "
          "  test-helper  test-helper1   -t-   -t-   --  "
          "  user         admin          ---   ---   st  "
          "  user         user1          st-   st-   --  "
          "  util         util1          st-   st-   --  "
          "  -            base1          stx   st-   --  "
          "  -            base2 *        stx   st-   --  "]
         (run-cmd "examples/profiles"
                  "info"
                  "changed-files:bases/base2/src/se/example/base2/core.clj"))))

(deftest profile-info-loc
  (is (= ["  stable since: 1234567                                 "
          "                                                        "
          "  projects: 2   interfaces: 5                           "
          "  bases:    2   components: 6                           "
          "                                                        "
          "  active profiles: default                              "
          "                                                        "
          "  project      alias  status   dev  extra   loc  (t)    "
          "  --------------------------   ----------   --------    "
          "  service      s       ---     ---   --       0    0    "
          "  development  dev     s--     s--   --       0    0    "
          "                                              0    0    "
          "                                                        "
          "  interface    brick           s    dev  extra   loc (t)"
          "  -------------------------   ---   ----------   -------"
          "  calculator   calculator1    s--   s--   --       1   0"
          "  database     database1      st-   st-   --       3   6"
          "  test-helper  test-helper1   -t-   -t-   --       3   3"
          "  user         admin          ---   ---   st       3   6"
          "  user         user1          st-   st-   --       3   5"
          "  util         util1          st-   st-   --       1   6"
          "  -            base1          st-   st-   --       1   7"
          "  -            base2          st-   st-   --       2   5"
          "                              14    14            17  38"]
         (run-cmd "examples/profiles"
                  "info" ":loc"
                  ":no-changes"))))

(deftest profile-info-skip-dev
  (is (= ["  stable since: 1234567          "
          "                                 "
          "  projects: 1   interfaces: 5    "
          "  bases:    2   components: 6    "
          "                                 "
          "  active profiles: default       "
          "                                 "
          "  project  alias  status         "
          "  ----------------------         "
          "  service  s       ---           "
          "                                 "
          "  interface    brick           s "
          "  -------------------------   ---"
          "  calculator   calculator1    s--"
          "  database     database1      st-"
          "  test-helper  test-helper1   -t-"
          "  user         admin          ---"
          "  user         user1          st-"
          "  util         util1          st-"
          "  -            base1          st-"
          "  -            base2          st-"]
         (run-cmd "examples/profiles"
                  "info" "skip:dev"
                  ":no-changes"))))

(deftest profile-deps
  (is (= ["                      t         "
          "                c     e         "
          "                a     s         "
          "                l  d  t         "
          "                c  a  -         "
          "                u  t  h         "
          "                l  a  e        b"
          "                a  b  l  u  u  a"
          "                t  a  p  s  t  s"
          "                o  s  e  e  i  e"
          "  brick         r  e  r  r  l  2"
          "  ------------------------------"
          "  admin         .  .  .  .  .  ."
          "  calculator1   .  .  .  .  .  ."
          "  database1     x  .  .  .  x  ."
          "  test-helper1  .  .  .  .  .  ."
          "  user1         .  t  t  .  .  ."
          "  util1         .  .  .  .  .  ."
          "  base1         .  .  .  .  .  t"
          "  base2         .  .  .  x  .  ."]
         (run-cmd "examples/profiles"
                  "deps"))))

(deftest profile-deps-project
  (is (= ["                      t         "
          "                c     e         "
          "                a     s         "
          "                l  d  t         "
          "                c  a  -         "
          "                u  t  h         "
          "                l  a  e         "
          "                a  b  l  u  u  b"
          "                t  a  p  s  t  a"
          "                o  s  e  e  i  s"
          "                r  e  r  r  l  e"
          "  brick         1  1  1  1  1  2"
          "  ------------------------------"
          "  calculator1   .  .  .  .  .  ."
          "  database1     x  .  .  .  x  ."
          "  test-helper1  .  .  .  .  .  ."
          "  user1         -  t  t  .  -  ."
          "  util1         .  .  .  .  .  ."
          "  base1         .  .  .  -  .  t"
          "  base2         .  .  .  x  .  ."]
         (run-cmd "examples/profiles"
                  "deps" "project:s"))))

(deftest profile-deps-brick
  (is (= ["  used by    <  database1  >  uses      "
          "  ---------                   ----------"
          "  user1 (t)                   calculator"
          "                              util      "]
         (run-cmd "examples/profiles"
                  "deps" "brick:database1"))))

(deftest profile-deps-project-brick
  (is (= ["  used by    <  database1  >  uses       "
          "  ---------                   -----------"
          "  user1 (t)                   calculator1"
          "                              util1      "]
         (run-cmd "examples/profiles"
                  "deps" "project:s" "brick:database1"))))

(deftest profile-libs
  (is (= ["                                                                      t"
          "                                                                      e"
          "                                                                      s"
          "                                                                      t"
          "                                                                      -"
          "                                                                      h"
          "                                                                      e"
          "                                                                      l"
          "                                                                      p"
          "                                                                      e"
          "                                                                      r"
          "  library              version  type   KB   s   dev  default  extra   1"
          "  ---------------------------------------   -   -------------------   -"
          "  clj-commons/fs       1.6.310  maven   -   -    -      x       -     ."
          "  metosin/malli        0.14.0   maven   -   t    x      -       -     x"
          "  org.clojure/clojure  1.11.1   maven   -   x    x      -       -     ."]
         (run-cmd "examples/profiles"
                  "libs" ":hide-lib-size"))))

(deftest profile-libs-skip-dev
  (is (= ["                                                t"
          "                                                e"
          "                                                s"
          "                                                t"
          "                                                -"
          "                                                h"
          "                                                e"
          "                                                l"
          "                                                p"
          "                                                e"
          "                                                r"
          "  library              version  type   KB   s   1"
          "  ---------------------------------------   -   -"
          "  clj-commons/fs       1.6.310  maven   -   -   ."
          "  metosin/malli        0.14.0   maven   -   t   x"
          "  org.clojure/clojure  1.11.1   maven   -   x   ."]
         (run-cmd "examples/profiles"
                  "libs" "skip:dev" ":hide-lib-size"))))

(deftest test-runner-inherit-test-runner-from-global
  (is (= ["{:create-test-runner"
          " [org.corfield.external-test-runner.interface/create],"
          " :setup-fn se.external.test-setup.interface/setup}"]
         (run-cmd "examples/test-runners"
                  "ws"
                  "get:projects:external-inherit-from-global:test"))))

(deftest test-runner-override-global-test-runner
  (is (= ["{:create-test-runner [polylith-kaocha.test-runner/create],"
          " :setup-fn se.external.test-setup.interface/setup}"]
         (run-cmd "examples/test-runners"
                  "ws"
                  "get:projects:kaocha-override-global:test"))))

(deftest error-107-missing-components
  (is (= ["  Error 107: Missing components in the service project for these interfaces: mycomp"]
         (run-cmd "examples/missing-component"
                  "check"))))

(defn clean-settings [ws]
  (let [vcs (dissoc (:vcs ws) :branch :stable-since)]
    (dissoc (assoc ws :vcs vcs)
            :user-config-filename
            :user-home)))

(deftest ws-get-settings
  (let [actual (clean-settings (ws-get "."
                                       [:settings]
                                       "ws-dir:examples/profiles"))]
    (is (= {:active-profiles      #{"default"}
            :color-mode           "none"
            :compact-views        #{}
            :default-profile-name "default"
            :empty-character      "."
            :interface-ns         "interface"
            :tag-patterns         {:release "v[0-9]*"
                                   :stable  "stable-*"}
            :thousand-separator   ","
            :top-namespace        "se.example"
            :vcs                  {:auto-add    true
                                   :git-root    ""
                                   :is-git-repo true
                                   :name        "git"
                                   :polylith    {:branch "master"
                                                 :repo   "https://github.com/polyfy/polylith.git"}}}
           (-> actual
               (dissoc :m2-dir)
               (update-in [:vcs :git-root] :git-root ""))))))

(deftest component-dependency-on-another-workspace
  (is (= {:src ["math" "s/util"], :test []}
         (read-string
           (run-cmd-plain "examples/multiple-workspaces/backend"
                          "ws"
                          "get:components:hello::interface-deps")))))

(deftest merge-test-configs
  (is (= {:create-test-runner [:default]
          :org.corfield/external-test-runner {:focus {:exclude [:integration :dummy]}}}
         (read-string
           (run-cmd-plain "examples/test-runners"
                          "ws"
                          "get:settings:test"
                          "with:default-test-runner:exclude-integration:exclude-dummy")))))

(deftest treat-component-from-other-workspace-as-component
  (is (= {:src ["howdy"
                 "math"
                 "s/util"]}
         (read-string
           (run-cmd-plain "examples/multiple-workspaces/backend"
                          "ws"
                          "get:projects:system:component-names")))))


(deftest mark-brick-from-another-workspace-as-brick
  (is (= {:alias     "s"
          :interface "util"
          :name      "util"
          :type      :component}
         (read-string
           (run-cmd-plain "examples/multiple-workspaces/backend"
                          "ws"
                          "get:projects:system:lib-deps:src:shared/util:brick")))))

(deftest mark-library-in-profile-as-brick-if-from-another-workspace
  (is (= {:alias     "s"
          :interface "share-me"
          :name      "share-me"
          :type      :component}
         (read-string
           (run-cmd-plain "examples/multiple-workspaces/backend"
                          "ws"
                          "get:profiles:default:lib-deps:shared/share-me:brick")))))