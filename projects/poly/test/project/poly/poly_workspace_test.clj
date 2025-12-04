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
  (is (= ["  project        alias  status   dev  extended"
          "  ----------------------------   -------------"
          "  poly *         poly    -t-     -t-     --   "
          "  polyx *        polyx   ---     ---     --   "
          "  development *  dev     s--     s--     --   "]
         (info/project-table (workspace) false false false))))

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
          "  ws-file-reader            ws-file-reader *             s--    s--    s--     --   "
          "  ws-updater                ws-updater *                 stx    s--    st-     --   "
          "  -                         nav-generator *              s--    ---    s--     --   "
          "  -                         poly-cli *                   stx    s--    st-     --   "]
         (info/brick-table (workspace) false false false))))

(defn keep-except [exclude rows]
  (filterv #(nil? (str/index-of % exclude))
           rows))

(deftest polylith-libs
  (is (= ["                                                                                                                 i                  "
          "                                                                                                                 m                  "
          "                                                                                                                 a                  "
          "                                                                                                                 g                  "
          "                                                                                                                 e                 w"
          "                                                                                                                 -                 s"
          "                                                                                                                 c           v  w  -"
          "                                                                                                                 r           a  o  e"
          "                                                                                                        c        e           l  r  x"
          "                                                                                                        r        a           i  k  p"
          "                                                                                                        e        t  m  s     d  s  l"
          "                                                                                                     a  a  d  f  o  a  h     a  p  o"
          "                                                                                                     n  t  e  i  r  v  e  t  t  a  r"
          "                                                                                                     t  o  p  l  -  e  l  a  o  c  e"
          "  library                          version     type      KB   poly  polyx   dev  default  extended   q  r  s  e  x  n  l  p  r  e  r"
          "  ---------------------------------------------------------   -----------   ----------------------   -------------------------------"
          "  babashka/fs                      0.5.30      maven     25    -      -      x      -        -       .  .  .  .  .  .  .  .  .  .  ."
          "  borkdude/edamame                 1.5.36      maven     26    x      x      x      -        -       .  .  .  x  .  .  .  .  .  .  ."
          "  borkdude/rewrite-edn             0.4.9       maven     11    -      -      x      -        -       .  .  .  .  .  .  .  .  .  .  ."
          "  cheshire/cheshire                6.1.0       maven     25    x      x      x      -        -       x  .  .  .  .  .  .  .  .  .  ."
          "  clj-commons/fs                   1.6.312     maven     12    x      x      x      -        -       .  .  .  x  .  .  .  .  .  .  ."
          "  clj-http/clj-http                3.13.1      maven     59    x      x      x      -        -       x  .  .  .  .  .  .  .  .  .  ."
          "  com.github.liquidz/antq          2.11.1276   maven     54    x      x      x      -        -       x  .  .  .  .  .  .  .  .  .  ."
          "  djblue/portal                    0.62.0      maven  1,927    x      x      x      -        -       .  .  .  .  .  .  .  x  .  .  ."
          "  lread/status-line                cf44c15     git       64    -      -      x      -        -       .  .  .  .  .  .  .  .  .  .  ."
          "  metosin/malli                    0.20.0      maven     95    x      x      x      -        -       .  .  .  .  .  .  .  .  x  .  ."
          "  mvxcvi/puget                     1.3.4       maven     15    x      x      x      -        -       .  .  .  .  .  .  .  .  .  .  x"
          "  org.apache.maven/maven-artifact  4.0.0-rc-5  maven     61    x      x      x      -        -       .  .  .  .  .  x  .  .  .  .  ."
          "  org.babashka/http-client         0.4.23      maven     15    -      -      x      -        -       .  .  .  .  .  .  .  .  .  .  ."
          "  org.clojure/clojure              1.12.3      maven  4,130    x      x      x      -        -       .  .  .  .  .  .  .  .  .  .  ."
          "  org.clojure/core.rrb-vector      0.2.0       maven     82    x      x      x      -        -       .  .  .  .  .  .  .  .  .  .  x"
          "  org.clojure/tools.deps           0.26.1553   maven     58    x      x      x      -        -       .  .  x  x  .  .  .  .  .  x  ."
          "  org.jline/jline                  3.30.6      maven  1,469    x      x      x      -        -       .  .  .  .  .  .  x  .  .  .  ."
          "  org.slf4j/slf4j-nop              2.0.17      maven      4    x      x      x      -        -       .  .  .  .  .  .  .  .  .  .  ."
          "  pjstadig/humane-test-output      0.11.0      maven      7    t      -      -      -        -       .  .  .  .  .  .  .  .  .  .  ."
          "  rewrite-clj/rewrite-clj          1.2.50      maven     78    -      -      x      -        -       .  .  .  .  .  .  .  .  .  .  ."
          "  selmer/selmer                    1.12.69     maven     64    x      x      x      -        -       .  x  .  .  .  .  .  .  .  .  ."
          "  version-clj/version-clj          2.0.3       maven     10    -      -      x      -        -       .  .  .  .  .  .  .  .  .  .  ."]
         (keep-except "clojure2d"
                      (libs/table (workspace))))))

(deftest polylith-workspace-ifc-deps-table
  (is (= ["                                                                                                          t                              "
          "                                                                                                          e                              "
          "                                                                                                          s                              "
          "                                                                                                          t                              "
          "                                                                                                       t  -                              "
          "                                                                                                       e  r                              "
          "                                                                                                       s  u                              "
          "                                                                                                       t  n                              "
          "                                                                                                       -  n                              "
          "                                                                                                       r  e                              "
          "                                                                                                       u  r                          w   "
          "                                           c                    i                                      n  -                          s   "
          "                                           o                    m                                      n  o                          -   "
          "                                           n                    a                 p                 t  e  r     u                 w  f   "
          "                                           f                    g                 a                 e  r  c  t  s  u              s  i  w"
          "                                           i                    e     i           t                 s  -  h  e  e  s     v     w  -  l  s"
          "                                           g                    -     n        o  h                 t  c  e  x  r  e     a     o  e  e  -"
          "                                     c     -  c                 c     t        v  -                 -  o  s  t  -  r     l  v  r  x  -  u"
          "                               c     o  c  r  r                 r     e        e  f        s        h  n  t  -  c  -     i  e  k  p  r  p"
          "                               h  c  m  o  e  e                 e     r     m  r  i     s  y        e  t  r  t  o  i     d  r  s  l  e  d"
          "                            a  a  h  m  m  a  a  d     f     h  a  i  f     a  v  n     h  s     t  l  r  a  a  n  n  u  a  s  p  o  a  a"
          "                            n  n  e  a  m  d  t  e  d  i  g  e  t  n  a  l  v  i  d     e  t  t  e  p  a  t  b  f  p  t  t  i  a  r  d  t"
          "                            t  g  c  n  o  e  o  p  o  l  i  l  o  f  c  i  e  e  e  s  l  e  a  s  e  c  o  l  i  u  i  o  o  c  e  e  e"
          "  brick                     q  e  k  d  n  r  r  s  c  e  t  p  r  o  e  b  n  w  r  h  l  m  p  t  r  t  r  e  g  t  l  r  n  e  r  r  r"
          "  ---------------------------------------------------------------------------------------------------------------------------------------"
          "  antq                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  api                       .  .  x  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  x  .  .  x  x  x  .  ."
          "  change                    .  .  .  .  x  .  .  .  .  .  x  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  check                     .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  ."
          "  clojure-test-test-runner  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  ."
          "  command                   .  .  x  .  x  x  x  x  x  x  x  x  .  x  .  x  .  x  .  .  x  .  x  .  .  .  x  .  x  .  x  .  x  x  x  .  ."
          "  common                    .  .  .  .  .  .  .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  x  .  x  .  .  .  ."
          "  config-reader             .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  ."
          "  creator                   .  .  .  .  x  .  .  .  .  x  x  .  .  .  .  .  .  .  .  .  .  .  .  .  t  .  .  .  .  .  x  .  x  .  .  .  ."
          "  deps                      .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  x  .  .  .  .  .  ."
          "  doc                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  ."
          "  file                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  git                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  help                      .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  x  .  x  .  .  .  ."
          "  image-creator             .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  image-creator-x           .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  info                      .  .  .  .  x  .  .  .  .  t  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  x  .  .  x  x  .  .  .  .  ."
          "  interface                 .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  lib                       x  .  .  .  x  x  .  .  .  x  .  .  .  .  .  .  x  .  .  .  .  .  .  .  t  .  .  x  x  t  x  .  .  t  .  .  ."
          "  maven                     .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  overview                  .  .  .  .  x  .  .  x  .  .  .  .  x  x  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  .  ."
          "  path-finder               .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  sh                        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  shell                     x  .  .  .  x  .  .  .  x  x  x  .  .  .  .  x  .  .  .  x  .  x  x  .  .  .  .  .  x  x  x  .  .  x  x  .  ."
          "  system                    .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  system-x                  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  tap                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  test                      .  .  .  .  x  .  .  .  .  .  x  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  test-helper               .  .  .  x  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  .  .  ."
          "  test-runner-contract      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  test-runner-orchestrator  .  .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  x  .  .  .  .  ."
          "  text-table                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  user-config               .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  user-input                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  util                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  validator                 .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  ."
          "  version                   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  workspace                 x  x  .  .  x  x  .  x  .  x  x  .  .  .  x  x  .  .  x  .  .  .  .  x  .  .  .  .  x  x  x  x  x  .  .  x  x"
          "  ws-explorer               .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  ws-file-reader            .  .  .  .  x  .  .  .  .  x  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  x  .  .  .  ."
          "  ws-updater                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  nav-generator             .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  poly-cli                  .  .  .  x  .  t  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  t  .  .  .  .  ."]
         (ws-ifc-deps-table/table (workspace)))))

(deftest polylith-workspace-project-deps-table
  (let [ws (workspace)
        projects (:projects ws)
        project (common/find-project "poly" projects)]
    (is (= ["                                                                                                          t                              "
            "                                                                                                          e                              "
            "                                                                                                          s                              "
            "                                                                                                          t                              "
            "                                                                                                       t  -                              "
            "                                                                                                       e  r                              "
            "                                                                                                       s  u                              "
            "                                                                                                       t  n                              "
            "                                                                                                       -  n                              "
            "                                                                                                       r  e                              "
            "                                                                                                       u  r                          w   "
            "                                           c                    i                                      n  -                          s   "
            "                                           o                    m                                      n  o                          -   "
            "                                           n                    a                 p                 t  e  r     u                 w  f   "
            "                                           f                    g                 a                 e  r  c  t  s  u              s  i  w"
            "                                           i                    e     i           t                 s  -  h  e  e  s     v     w  -  l  s"
            "                                           g                    -     n        o  h                 t  c  e  x  r  e     a     o  e  e  -"
            "                                     c     -  c                 c     t        v  -                 -  o  s  t  -  r     l  v  r  x  -  u"
            "                               c     o  c  r  r                 r     e        e  f        s        h  n  t  -  c  -     i  e  k  p  r  p"
            "                               h  c  m  o  e  e                 e     r     m  r  i     s  y        e  t  r  t  o  i     d  r  s  l  e  d"
            "                            a  a  h  m  m  a  a  d     f     h  a  i  f     a  v  n     h  s     t  l  r  a  a  n  n  u  a  s  p  o  a  a"
            "                            n  n  e  a  m  d  t  e  d  i  g  e  t  n  a  l  v  i  d     e  t  t  e  p  a  t  b  f  p  t  t  i  a  r  d  t"
            "                            t  g  c  n  o  e  o  p  o  l  i  l  o  f  c  i  e  e  e  s  l  e  a  s  e  c  o  l  i  u  i  o  o  c  e  e  e"
            "  brick                     q  e  k  d  n  r  r  s  c  e  t  p  r  o  e  b  n  w  r  h  l  m  p  t  r  t  r  e  g  t  l  r  n  e  r  r  r"
            "  ---------------------------------------------------------------------------------------------------------------------------------------"
            "  antq                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  api                       +  +  x  .  x  +  .  +  .  +  +  .  +  .  +  +  +  .  +  +  .  +  .  +  .  +  x  +  +  x  +  +  x  x  x  +  +"
            "  change                    .  .  .  .  x  .  .  .  .  +  x  .  +  .  .  .  .  .  x  +  .  +  .  .  .  .  .  +  +  .  x  .  +  .  .  .  ."
            "  check                     .  .  .  .  +  .  .  .  .  +  .  .  +  .  .  .  .  .  +  .  .  +  .  .  .  +  .  +  +  .  x  x  +  .  .  .  ."
            "  clojure-test-test-runner  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  ."
            "  command                   +  +  x  .  x  x  x  x  x  x  x  x  +  x  +  x  +  x  +  +  x  +  x  +  .  +  x  +  x  +  x  +  x  x  x  +  +"
            "  common                    .  .  .  .  .  .  .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  +  .  .  .  .  .  x  x  .  x  .  x  .  .  .  ."
            "  config-reader             .  .  .  .  x  .  .  .  .  x  .  .  +  .  .  .  .  .  +  .  .  +  .  .  .  +  .  +  +  .  x  x  +  .  .  .  ."
            "  creator                   -  -  -  -  x  -  -  -  -  x  x  -  +  -  -  -  -  -  -  +  -  +  -  -  t  -  -  +  +  -  x  -  x  -  -  -  -"
            "  deps                      .  .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  .  .  .  +  .  .  .  .  .  x  x  .  x  .  +  .  .  .  ."
            "  doc                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  +  .  .  .  .  .  .  .  .  .  .  x  .  .  .  ."
            "  file                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  git                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  help                      .  .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  .  .  .  x  .  .  .  .  .  +  +  .  x  .  x  .  .  .  ."
            "  image-creator             .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  info                      .  .  .  .  x  .  .  .  .  t  .  .  +  .  .  .  .  .  x  .  .  +  .  .  .  +  .  x  +  .  x  x  +  .  .  .  ."
            "  interface                 .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  lib                       x  -  -  -  x  x  -  -  -  x  -  -  +  -  -  -  x  -  +  -  -  +  -  -  t  +  -  x  x  t  x  +  +  t  -  -  -"
            "  maven                     .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  overview                  +  .  .  .  x  +  .  x  .  +  .  .  x  x  .  x  +  .  +  .  .  +  .  .  .  +  .  +  +  x  x  +  +  .  .  .  ."
            "  path-finder               .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  sh                        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  shell                     x  +  .  .  x  +  .  +  x  x  x  .  +  .  +  x  +  .  +  x  .  x  x  +  .  +  .  +  x  x  x  +  +  x  x  +  +"
            "  system                    .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  tap                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  test                      .  .  .  .  x  .  .  .  .  +  x  .  +  .  .  .  .  .  x  +  .  +  .  .  .  .  .  +  +  .  +  .  +  .  .  .  ."
            "  test-helper               -  -  -  t  -  -  -  -  -  t  -  -  -  -  -  -  -  -  -  -  -  -  -  -  .  -  -  -  t  t  -  -  -  -  -  -  -"
            "  test-runner-contract      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  test-runner-orchestrator  .  .  .  .  x  .  .  x  .  +  .  .  +  .  .  .  .  .  +  .  .  +  .  .  .  x  .  +  +  .  x  x  +  .  .  .  ."
            "  text-table                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  user-config               .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  user-input                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  util                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  validator                 .  .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  x  .  .  +  .  .  .  x  .  +  +  .  x  .  +  .  .  .  ."
            "  version                   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  workspace                 x  x  .  .  x  x  .  x  .  x  x  .  +  .  x  x  +  .  x  +  .  +  .  x  .  +  .  +  x  x  x  x  x  .  .  x  x"
            "  ws-explorer               .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  ws-file-reader            .  .  .  .  x  .  .  .  .  x  x  .  +  .  .  .  .  .  .  +  .  +  .  .  .  .  .  +  +  .  x  .  x  .  .  .  ."
            "  ws-updater                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  nav-generator             .  .  .  .  +  x  .  .  .  +  .  .  +  .  .  .  .  .  +  .  .  +  .  .  .  +  .  +  +  .  x  +  +  .  .  .  ."
            "  poly-cli                  +  +  +  x  +  t  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  .  +  +  +  +  x  x  t  +  +  +  +  +"]
           (ws-project-deps-table/table ws project)))))

(deftest polylith-workspace-project-deps-table-indirect
  (let [ws (workspace)
        projects (:projects ws)
        project (common/find-project "poly" projects)]
    (is (= ["                                                                                                          t                              "
            "                                                                                                          e                              "
            "                                                                                                          s                              "
            "                                                                                                          t                              "
            "                                                                                                       t  -                              "
            "                                                                                                       e  r                              "
            "                                                                                                       s  u                              "
            "                                                                                                       t  n                              "
            "                                                                                                       -  n                              "
            "                                                                                                       r  e                              "
            "                                                                                                       u  r                          w   "
            "                                           c                    i                                      n  -                          s   "
            "                                           o                    m                                      n  o                          -   "
            "                                           n                    a                 p                 t  e  r     u                 w  f   "
            "                                           f                    g                 a                 e  r  c  t  s  u              s  i  w"
            "                                           i                    e     i           t                 s  -  h  e  e  s     v     w  -  l  s"
            "                                           g                    -     n        o  h                 t  c  e  x  r  e     a     o  e  e  -"
            "                                     c     -  c                 c     t        v  -                 -  o  s  t  -  r     l  v  r  x  -  u"
            "                               c     o  c  r  r                 r     e        e  f        s        h  n  t  -  c  -     i  e  k  p  r  p"
            "                               h  c  m  o  e  e                 e     r     m  r  i     s  y        e  t  r  t  o  i     d  r  s  l  e  d"
            "                            a  a  h  m  m  a  a  d     f     h  a  i  f     a  v  n     h  s     t  l  r  a  a  n  n  u  a  s  p  o  a  a"
            "                            n  n  e  a  m  d  t  e  d  i  g  e  t  n  a  l  v  i  d     e  t  t  e  p  a  t  b  f  p  t  t  i  a  r  d  t"
            "                            t  g  c  n  o  e  o  p  o  l  i  l  o  f  c  i  e  e  e  s  l  e  a  s  e  c  o  l  i  u  i  o  o  c  e  e  e"
            "  brick                     q  e  k  d  n  r  r  s  c  e  t  p  r  o  e  b  n  w  r  h  l  m  p  t  r  t  r  e  g  t  l  r  n  e  r  r  r"
            "  ---------------------------------------------------------------------------------------------------------------------------------------"
            "  antq                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  api                       +  +  x  .  x  +  .  +  .  +  +  .  +  .  +  +  +  .  +  +  .  +  .  +  .  +  x  +  +  x  +  +  x  x  x  +  +"
            "  change                    .  .  .  .  x  .  .  .  .  +  x  .  +  .  .  .  .  .  x  +  .  +  .  .  .  .  .  +  +  .  x  .  +  .  .  .  ."
            "  check                     .  .  .  .  +  .  .  .  .  +  .  .  +  .  .  .  .  .  +  .  .  +  .  .  .  +  .  +  +  .  x  x  +  .  .  .  ."
            "  clojure-test-test-runner  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  ."
            "  command                   +  +  x  .  x  x  x  x  x  x  x  x  +  x  +  x  +  x  +  +  x  +  x  +  .  +  x  +  x  +  x  +  x  x  x  +  +"
            "  common                    .  .  .  .  .  .  .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  +  .  .  .  .  .  x  x  .  x  .  x  .  .  .  ."
            "  config-reader             .  .  .  .  x  .  .  .  .  x  .  .  +  .  .  .  .  .  +  .  .  +  .  .  .  +  .  +  +  .  x  x  +  .  .  .  ."
            "  creator                   -  -  -  -  x  -  -  -  -  x  x  -  +  -  -  -  -  -  -  +  -  +  -  -  t  -  -  +  +  -  x  -  x  -  -  -  -"
            "  deps                      .  .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  .  .  .  +  .  .  .  .  .  x  x  .  x  .  +  .  .  .  ."
            "  doc                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  +  .  .  .  .  .  .  .  .  .  .  x  .  .  .  ."
            "  file                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  git                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  help                      .  .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  .  .  .  x  .  .  .  .  .  +  +  .  x  .  x  .  .  .  ."
            "  image-creator             .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  info                      .  .  .  .  x  .  .  .  .  t  .  .  +  .  .  .  .  .  x  .  .  +  .  .  .  +  .  x  +  .  x  x  +  .  .  .  ."
            "  interface                 .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  lib                       x  -  -  -  x  x  -  -  -  x  -  -  +  -  -  -  x  -  +  -  -  +  -  -  t  +  -  x  x  t  x  +  +  t  -  -  -"
            "  maven                     .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  overview                  +  .  .  .  x  +  .  x  .  +  .  .  x  x  .  x  +  .  +  .  .  +  .  .  .  +  .  +  +  x  x  +  +  .  .  .  ."
            "  path-finder               .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  sh                        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  shell                     x  +  .  .  x  +  .  +  x  x  x  .  +  .  +  x  +  .  +  x  .  x  x  +  .  +  .  +  x  x  x  +  +  x  x  +  +"
            "  system                    .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  tap                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  test                      .  .  .  .  x  .  .  .  .  +  x  .  +  .  .  .  .  .  x  +  .  +  .  .  .  .  .  +  +  .  +  .  +  .  .  .  ."
            "  test-helper               -  -  -  t  -  -  -  -  -  t  -  -  -  -  -  -  -  -  -  -  -  -  -  -  .  -  -  -  t  t  -  -  -  -  -  -  -"
            "  test-runner-contract      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  test-runner-orchestrator  .  .  .  .  x  .  .  x  .  +  .  .  +  .  .  .  .  .  +  .  .  +  .  .  .  x  .  +  +  .  x  x  +  .  .  .  ."
            "  text-table                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  user-config               .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  user-input                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  util                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  validator                 .  .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  x  .  .  +  .  .  .  x  .  +  +  .  x  .  +  .  .  .  ."
            "  version                   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  workspace                 x  x  .  .  x  x  .  x  .  x  x  .  +  .  x  x  +  .  x  +  .  +  .  x  .  +  .  +  x  x  x  x  x  .  .  x  x"
            "  ws-explorer               .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  ws-file-reader            .  .  .  .  x  .  .  .  .  x  x  .  +  .  .  .  .  .  .  +  .  +  .  .  .  .  .  +  +  .  x  .  x  .  .  .  ."
            "  ws-updater                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  nav-generator             .  .  .  .  +  x  .  .  .  +  .  .  +  .  .  .  .  .  +  .  .  +  .  .  .  +  .  +  +  .  x  +  +  .  .  .  ."
            "  poly-cli                  +  +  +  x  +  t  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  .  +  +  +  +  x  x  t  +  +  +  +  +"]
           (ws-project-deps-table/table ws project)))))

(deftest polylith-brick-and-project-deps
  (let [{:keys [components projects] :as ws} (workspace)
        project (common/find-project "poly" projects)
        brick (common/find-component "workspace" components)]
    (is (= ["  used by  <  workspace  >  uses          "
            "  -------                   --------------"
            "  api                       antq          "
            "  command                   change        "
            "  lib (t)                   common        "
            "  shell                     config-reader "
            "                            deps          "
            "                            file          "
            "                            git           "
            "                            interface     "
            "                            lib           "
            "                            path-finder   "
            "                            test          "
            "                            user-config   "
            "                            user-input    "
            "                            util          "
            "                            validator     "
            "                            version       "
            "                            ws-file-reader"
            "                            ws-updater    "]
           (brick-deps-table/table ws project brick "none")))))

(deftest polylith-project-brick-deps
  (let [{:keys [components] :as ws} (workspace)
        brick (common/find-component "workspace" components)]
    (is (= ["  used by  <  workspace  >  uses          "
            "  -------                   --------------"
            "  api                       antq          "
            "  command                   change        "
            "  lib (t)                   common        "
            "  shell                     config-reader "
            "                            deps          "
            "                            file          "
            "                            git           "
            "                            interface     "
            "                            lib           "
            "                            path-finder   "
            "                            test          "
            "                            user-config   "
            "                            user-input    "
            "                            util          "
            "                            validator     "
            "                            version       "
            "                            ws-file-reader"
            "                            ws-updater    "]
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
                                                        "ws-file-reader"
                                                        "ws-updater"]}
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
                                                        "file"
                                                        "image-creator"
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
                                                        "ws-file-reader"
                                                        "ws-updater"]}
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
                                                        "ws-file-reader"
                                                        "ws-updater"]}}
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
                                             :indirect ["image-creator"
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
                                             :indirect ["image-creator"
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
                                                        "ws-file-reader"
                                                        "ws-updater"]}}
          "deps"                     {:src  {:direct   ["common"
                                                        "text-table"
                                                        "user-config"
                                                        "util"]
                                             :indirect ["file"
                                                        "image-creator"
                                                        "system"
                                                        "version"]}
                                      :test {:direct   ["common"
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
          "image-creator"            {:src  {}
                                      :test {}}
          "info"                     {:src  {:direct   ["common"
                                                        "path-finder"
                                                        "text-table"
                                                        "util"
                                                        "validator"]
                                             :indirect ["file"
                                                        "image-creator"
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
                                             :indirect ["image-creator"
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
                                             :indirect ["image-creator"
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
                                                        "user-input"
                                                        "util"
                                                        "workspace"]
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
                                                        "validator"
                                                        "version"
                                                        "ws-explorer"
                                                        "ws-file-reader"
                                                        "ws-updater"]}}
          "maven"                    {:src  {}
                                      :test {}}
          "nav-generator"            {:src  {:direct   ["config-reader"
                                                        "util"]
                                             :indirect ["common"
                                                        "file"
                                                        "image-creator"
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
                                                        "ws-file-reader"
                                                        "ws-updater"]}
                                      :test {:direct   ["command"
                                                        "config-reader"
                                                        "user-input"
                                                        "util"
                                                        "validator"]
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
                                                        "workspace"
                                                        "ws-explorer"
                                                        "ws-file-reader"
                                                        "ws-updater"]}}
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
                                                        "ws-file-reader"
                                                        "ws-updater"]}
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
                                                        "ws-file-reader"
                                                        "ws-updater"]}}
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
                                                        "ws-file-reader"
                                                        "ws-updater"]}}
          "test-runner-contract"     {:src  {:direct ["util"]}
                                      :test {:direct ["util"]}}
          "test-runner-orchestrator" {:src  {:direct   ["common"
                                                        "deps"
                                                        "test-runner-contract"
                                                        "util"
                                                        "validator"]
                                             :indirect ["file"
                                                        "image-creator"
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
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "util"]
                                             :indirect ["file"
                                                        "image-creator"
                                                        "system"
                                                        "text-table"
                                                        "user-config"
                                                        "version"]}
                                      :test {:direct   ["common"
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "util"]
                                             :indirect ["file"
                                                        "image-creator"
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
                                                        "ws-file-reader"
                                                        "ws-updater"]
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
                                                        "ws-file-reader"
                                                        "ws-updater"]
                                             :indirect ["image-creator"
                                                        "maven"
                                                        "sh"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"]}}
          "ws-explorer"              {:src  {:direct ["util"]}
                                      :test {:direct ["util"]}}
          "ws-file-reader"           {:src  {:direct   ["common"
                                                        "file"
                                                        "git"
                                                        "util"
                                                        "version"]
                                             :indirect ["image-creator"
                                                        "sh"
                                                        "system"
                                                        "text-table"
                                                        "user-config"]}
                                      :test {}}
          "ws-updater"               {:src  {}
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
          "components/ws-file-reader/src"
          "components/ws-updater/src"]
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
          "components/ws-updater/test"
          "projects/poly/test"]
         (ws-explorer/extract (workspace) ["projects" "poly" "paths" "test"]))))

(deftest polylith-poly-project-lib-imports
  (is (= {:src ["antq.api"
                "cheshire.core"
                "clj-http.client"
                "clojure.data.json"
                "clojure.edn"
                "clojure.instant"
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
                "org.apache.maven.artifact.versioning"
                "org.jline.reader"
                "org.jline.reader.impl"
                "org.jline.terminal"
                "portal.api"
                "puget.printer"
                "selmer.parser"]
          :test ["clojure.lang"
                 "clojure.string"
                 "clojure.test"
                 "malli.core"
                 "me.raynes.fs"]}
         (ws-explorer/extract (workspace) ["projects" "poly" "lib-imports"]))))

(deftest polylith-shell-component-lib-deps
  (is (= {:src {"org.jline/jline" {:size    1505052
                                   :type    "maven"
                                   :version "3.30.6"}}}
         (ws-explorer/extract (workspace) ["components" "shell" "lib-deps"]))))

(deftest profile-info
  (is (= ["  stable since: 1234567                        "
          "                                               "
          "  projects: 2   interfaces: 5                  "
          "  bases:    2   components: 7                  "
          "                                               "
          "  active profiles: default                     "
          "                                               "
          "  project      alias  status   dev  ex/tra     "
          "  --------------------------   -----------     "
          "  service      s       -t-     -t-    --       "
          "  development  dev     s--     s--    --       "
          "                                               "
          "  interface    brick           s    dev  ex/tra"
          "  -------------------------   ---   -----------"
          "  calculator   calculator1    s--   s--    --  "
          "  database     database1      st-   st-    --  "
          "  database     database2      ---   ---    st  "
          "  test-helper  test-helper1   -t-   -t-    --  "
          "  user         admin          ---   ---    st  "
          "  user         user1          st-   st-    --  "
          "  util         util1          st-   st-    --  "
          "  -            base1          st-   st-    --  "
          "  -            base2          st-   st-    --  "]
         (run-cmd "examples/profiles"
                  "info"
                  ":no-changes"))))

(deftest profile-info-where-test-has-changed
  (is (= ["  stable since: 1234567                        "
          "                                               "
          "  projects: 2   interfaces: 5                  "
          "  bases:    2   components: 7                  "
          "                                               "
          "  active profiles: default                     "
          "                                               "
          "  project        alias  status   dev  ex/tra   "
          "  ----------------------------   -----------   "
          "  service +      s       -t-     -t-    --     "
          "  development +  dev     s--     s--    --     "
          "                                               "
          "  interface    brick           s    dev  ex/tra"
          "  -------------------------   ---   -----------"
          "  calculator   calculator1    s--   s--    --  "
          "  database     database1      st-   st-    --  "
          "  database     database2      ---   ---    st  "
          "  test-helper  test-helper1   -t-   -t-    --  "
          "  user         admin          ---   ---    st  "
          "  user         user1          st-   st-    --  "
          "  util         util1          st-   st-    --  "
          "  -            base1          stx   st-    --  "
          "  -            base2 *        stx   st-    --  "]
         (run-cmd "examples/profiles"
                  "info"
                  "changed-files:bases/base2/test/se/example/base2/core_test.clj"))))

(deftest profile-info-where-src-has-changed
  (is (= ["  stable since: 1234567                        "
          "                                               "
          "  projects: 2   interfaces: 5                  "
          "  bases:    2   components: 7                  "
          "                                               "
          "  active profiles: default                     "
          "                                               "
          "  project        alias  status   dev  ex/tra   "
          "  ----------------------------   -----------   "
          "  service +      s       -t-     -t-    --     "
          "  development +  dev     s--     s--    --     "
          "                                               "
          "  interface    brick           s    dev  ex/tra"
          "  -------------------------   ---   -----------"
          "  calculator   calculator1    s--   s--    --  "
          "  database     database1      st-   st-    --  "
          "  database     database2      ---   ---    st  "
          "  test-helper  test-helper1   -t-   -t-    --  "
          "  user         admin          ---   ---    st  "
          "  user         user1          st-   st-    --  "
          "  util         util1          st-   st-    --  "
          "  -            base1          stx   st-    --  "
          "  -            base2 *        stx   st-    --  "]
         (run-cmd "examples/profiles"
                  "info"
                  "changed-files:bases/base2/src/se/example/base2/core.clj"))))

(deftest profile-info-loc
  (is (= ["  stable since: 1234567                                  "
          "                                                         "
          "  projects: 2   interfaces: 5                            "
          "  bases:    2   components: 7                            "
          "                                                         "
          "  active profiles: default                               "
          "                                                         "
          "  project      alias  status   dev  ex/tra   loc  (t)    "
          "  --------------------------   -----------   --------    "
          "  service      s       -t-     -t-    --       0    8    "
          "  development  dev     s--     s--    --       0    0    "
          "                                               0    8    "
          "                                                         "
          "  interface    brick           s    dev  ex/tra   loc (t)"
          "  -------------------------   ---   -----------   -------"
          "  calculator   calculator1    s--   s--    --       1   0"
          "  database     database1      st-   st-    --       6   6"
          "  database     database2      ---   ---    st       4   5"
          "  test-helper  test-helper1   -t-   -t-    --       3   3"
          "  user         admin          ---   ---    st       3   6"
          "  user         user1          st-   st-    --       3   8"
          "  util         util1          st-   st-    --       1   6"
          "  -            base1          st-   st-    --       1   7"
          "  -            base2          st-   st-    --       2   5"
          "                              17    17             24  46"]
         (run-cmd "examples/profiles"
                  "info" ":loc"
                  ":no-changes"))))

(deftest profile-info-skip-dev
  (is (= ["  stable since: 1234567          "
          "                                 "
          "  projects: 1   interfaces: 5    "
          "  bases:    2   components: 7    "
          "                                 "
          "  active profiles: default       "
          "                                 "
          "  project  alias  status         "
          "  ----------------------         "
          "  service  s       -t-           "
          "                                 "
          "  interface    brick           s "
          "  -------------------------   ---"
          "  calculator   calculator1    s--"
          "  database     database1      st-"
          "  database     database2      ---"
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
          "  database2     .  .  .  .  .  ."
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
  (is (= ["                                                                       t"
          "                                                                       e"
          "                                                                       s"
          "                                                                       t"
          "                                                                       -"
          "                                                                       h"
          "                                                                       e"
          "                                                                       l"
          "                                                                       p"
          "                                                                       e"
          "                                                                       r"
          "  library              version  type   KB   s   dev  default  ex/tra   1"
          "  ---------------------------------------   -   --------------------   -"
          "  clj-commons/fs       1.6.310  maven   -   -    -      x       -      ."
          "  metosin/malli        0.19.2   maven   -   t    x      -       -      x"
          "  org.clojure/clojure  1.12.3   maven   -   x    x      -       -      ."]
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
          "  metosin/malli        0.19.2   maven   -   t   x"
          "  org.clojure/clojure  1.12.3   maven   -   x   ."]
         (run-cmd "examples/profiles"
                  "libs" "skip:dev" ":hide-lib-size"))))

(deftest mix-example
  (is (= ["                                           c"
          "                                           o"
          "                                           m"
          "                            c              p"
          "                            o              -"
          "                            m              c"
          "                            p              l"
          "                            -        c  c  j"
          "                         c  c  c     o  o  s"
          "                         o  l  o     m  m  -"
          "                         m  j  m     p  p  w"
          "                         p  -  p     -  -  i"
          "                         -  c  -  c  c  c  t"
          "                         c  l  c  o  l  l  h"
          "                         l  j  l  m  j  j  -"
          "                         j  c  j  p  s  s  m"
          "                         -  -  -  -  -  -  a"
          "                         c  c  o  c  c  o  c"
          "                         l  l  n  l  l  n  r"
          "                         j  j  l  j  j  l  o"
          "  brick                  c  s  y  c  c  y  s"
          "  ------------------------------------------"
          "  comp-clj-cljc          .  .  .  .  .  .  ."
          "  comp-clj-cljc-cljs     .  .  .  .  .  .  ."
          "  comp-clj-only          .  .  .  x  .  .  ."
          "  comp-cljc              .  x  .  .  .  .  ."
          "  comp-cljs-cljc         .  .  .  .  .  .  ."
          "  comp-cljs-only         .  .  .  x  .  .  x"
          "  comp-cljs-with-macros  .  .  .  .  .  .  ."
          "  clj-base               x  .  x  .  .  .  ."
          "  cljs-base              .  .  .  .  x  x  ."]
         (run-cmd "examples/mix-example"
                  "deps"))))


(deftest test-runner-inherit-test-runner-from-global
  (is (= ["{:create-test-runner"
          " [org.corfield.external-test-runner.interface/create],"
          " :setup-fn se.external.test-setup.interface/setup,"
          " :org.corfield/external-test-runner {:focus {:exclude [:dummy]}}}"]
         (run-cmd "examples/test-runners"
                  "ws"
                  "get:projects:external-inherit-from-global:test"
                  "with:exclude-dummy"))))

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
            :tag-patterns         {:release "^v[0-9].*"
                                   :stable  "^stable-.*"}
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

(deftest merge-test-configs
  (is (= {:create-test-runner [:default]
          :org.corfield/external-test-runner {:focus {:exclude [:integration :dummy]}}}
         (read-string
           (run-cmd-plain "examples/test-runners"
                          "ws"
                          "get:settings:test"
                          "with:default-test-runner:exclude-integration:exclude-dummy")))))

(deftest illegal-brick-dependencies
  (is (= [{:brick             "mybase"
           :code              112
           :colorized-message "Illegal dependency on brick from the 'mybase' base (in deps.edn): ../../components/util"
           :message           "Illegal dependency on brick from the 'mybase' base (in deps.edn): ../../components/util"
           :type              "error"}
          {:brick             "stuff"
           :code              112
           :colorized-message "Illegal dependency on brick from the 'stuff' component (in deps.edn): ../../components/util/src"
           :message           "Illegal dependency on brick from the 'stuff' component (in deps.edn): ../../components/util/src"
           :type              "error"}]

         (read-string
           (run-cmd-plain "examples/illegal-brick-deps"
                          "ws"
                          "get:messages")))))

(deftest set-and-get-value-in-workspace
  (is (= :error
         (read-string
           (run-cmd-plain "examples/inconsistent-libs"
                          "ws"
                          "get:configs:workspace:validations:inconsistent-lib-versions:type"
                          "set:configs:workspace:validations:inconsistent-lib-versions:type"
                          "value:error"
                          "type:keyword")))))
