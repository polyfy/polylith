(ns project.poly.poly-workspace-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.text-table.brick-deps-table :as brick-ifc-deps]
            [polylith.clj.core.deps.text-table.brick-project-deps-table :as brick-deps-table]
            [polylith.clj.core.deps.text-table.workspace-project-deps-table :as ws-project-deps-table]
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

(defn run-cmd [ws-dir cmd & args]
  (let [input (user-input/extract-params (concat [cmd] args [(str "ws-dir:" ws-dir) "fake-sha:1234567" "fake-tag:" "color-mode:none"]))]
    (str/split-lines
      (with-out-str
        (-> input command/execute-command)))))

(defn ws-get [ws-dir ws-param & args]
  (let [workspace (-> (user-input/extract-params (concat ["version" (str "ws-dir:" ws-dir) "color-mode:none"] args))
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-changes)]
    (get-in workspace ws-param)))

(deftest polylith-project-table
  (is (= (project-table/table (workspace) false false)
         ["  project        alias  status   dev  extended   "
          "  ----------------------------   -------------   "
          "  poly *         poly    -t-     -t-     --      "
          "  polyx *        polyx   ---     ---     --      "
          "  development *  dev     s--     s--     --      "])))

(deftest polylith-info
  (is (= (ws-table/table (workspace) false false)
         ["  interface                 brick                        poly  polyx   dev  extended"
          "  ----------------------------------------------------   -----------   -------------"
          "  antq                      antq *                       s--    s--    s--     --   "
          "  api                       api *                        stx    ---    st-     --   "
          "  change                    change *                     stx    s--    st-     --   "
          "  clojure-test-test-runner  clojure-test-test-runner *   stx    s--    st-     --   "
          "  command                   command *                    stx    s--    st-     --   "
          "  common                    common *                     stx    s--    st-     --   "
          "  config-reader             config-reader *              stx    s--    st-     --   "
          "  creator                   creator *                    stx    s--    st-     --   "
          "  deps                      deps *                       stx    s--    st-     --   "
          "  doc                       doc *                        s--    s--    s--     --   "
          "  file                      file *                       stx    s--    st-     --   "
          "  git                       git *                        stx    s--    st-     --   "
          "  help                      help *                       s--    s--    s--     --   "
          "  image-creator             image-creator *              s--    ---    s--     --   "
          "  image-creator             image-creator-x *            ---    s--    ---     s-   "
          "  lib                       lib *                        stx    s--    st-     --   "
          "  migrator                  migrator *                   stx    s--    st-     --   "
          "  overview                  overview *                   s--    s--    s--     --   "
          "  path-finder               path-finder *                stx    s--    st-     --   "
          "  sh                        sh *                         s--    s--    s--     --   "
          "  shell                     shell *                      stx    s--    st-     --   "
          "  system                    system *                     s--    ---    s--     --   "
          "  system                    system-x *                   ---    s--    ---     s-   "
          "  tap                       tap *                        s--    s--    s--     --   "
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
          "  workspace-clj             workspace-clj *              stx    s--    st-     --   "
          "  ws-explorer               ws-explorer *                stx    s--    st-     --   "
          "  ws-file                   ws-file *                    s--    s--    s--     --   "
          "  -                         nav-generator *              s--    ---    s--     --   "
          "  -                         poly-cli *                   stx    s--    st-     --   "])))

(defn keep-except [exclude rows]
  (filterv #(nil? (str/index-of % exclude))
           rows))

(deftest polylith-libs
  (is (= (keep-except "clojure2d"
                      (libs/table (workspace) false))
         ["                                                                                                         i                  "
          "                                                                                                         m                  "
          "                                                                                                         a              w   "
          "                                                                                                         g              o   "
          "                                                                                                         e              r  w"
          "                                                                                                         -              k  s"
          "                                                                                                         c           v  s  -"
          "                                                                                                         r  m        a  p  e"
          "                                                                                                         e  i        l  a  x"
          "                                                                                                         a  g        i  c  p"
          "                                                                                                         t  r  s     d  e  l"
          "                                                                                                a  d  f  o  a  h     a  -  o"
          "                                                                                                n  e  i  r  t  e  t  t  c  r"
          "                                                                                                t  p  l  -  o  l  a  o  l  e"
          "  library                      version    type      KB   poly  polyx   dev  extended  default   q  s  e  x  r  l  p  r  j  r"
          "  ----------------------------------------------------   -----------   ----------------------   ----------------------------"
          "  borkdude/edamame             1.3.23     maven     24    x      x      x      -         -      .  .  x  .  .  .  .  .  .  ."
          "  clj-commons/fs               1.6.310    maven     12    x      x      x      -         -      .  .  x  .  .  .  .  .  .  ."
          "  com.github.liquidz/antq      2.5.1095   maven     50    x      x      x      -         -      x  .  .  .  .  .  .  .  .  ."
          "  djblue/portal                0.45.1     maven  1,822    x      x      x      -         -      .  .  .  .  .  .  x  .  .  ."
          "  metosin/malli                0.11.0     maven     85    x      x      x      -         -      .  .  .  .  .  .  .  x  .  ."
          "  mount/mount                  0.1.17     maven      8    -      -      x      -         -      .  .  .  .  .  .  .  .  .  ."
          "  mvxcvi/puget                 1.3.4      maven     15    x      x      x      -         -      .  .  .  .  .  .  .  .  .  x"
          "  org.clojure/clojure          1.11.1     maven  4,008    x      x      x      -         -      .  .  .  .  .  .  .  .  .  ."
          "  org.clojure/tools.deps       0.18.1354  maven     58    x      x      x      -         -      .  x  x  .  .  .  .  .  x  ."
          "  org.jline/jline              3.21.0     maven    971    x      x      x      -         -      .  .  .  .  .  x  .  .  .  ."
          "  org.slf4j/slf4j-nop          2.0.7      maven      4    x      x      x      -         -      .  .  .  .  .  .  .  .  .  ."
          "  pjstadig/humane-test-output  0.11.0     maven      7    t      -      -      -         -      .  .  .  .  .  .  .  .  .  ."
          "  rewrite-clj/rewrite-clj      1.1.47     maven     73    -      -      x      -         -      .  .  .  .  .  .  .  .  .  ."
          "  zprint/zprint                1.2.7      maven    210    x      x      x      -         -      .  .  .  .  x  .  .  .  .  ."])))

#_(deftest polylith-libs-outdated
    (is (= (keep-except "clojure2d"
                        (libs/table (workspace) true))
           ["                                                                                                                      i                  "
            "                                                                                                                      m                  "
            "                                                                                                                      a              w   "
            "                                                                                                                      g              o   "
            "                                                                                                                      e              r  w"
            "                                                                                                                      -              k  s"
            "                                                                                                                      c           v  s  -"
            "                                                                                                                      r  m        a  p  e"
            "                                                                                                                      e  i        l  a  x"
            "                                                                                                                      a  g        i  c  p"
            "                                                                                                                      t  r  s     d  e  l"
            "                                                                                                             a  d  f  o  a  h     a  -  o"
            "                                                                                                             n  e  i  r  t  e  t  t  c  r"
            "                                                                                                             t  p  l  -  o  l  a  o  l  e"
            "  library                      version    latest  type      KB   api  poly  polyx   dev  extended  default   q  s  e  x  r  l  p  r  j  r"
            "  ------------------------------------------------------------   ----------------   ----------------------   ----------------------------"
            "  borkdude/edamame             1.3.23             maven     24    x    x      x      x      -         -      .  .  x  .  .  .  .  .  .  ."
            "  clj-commons/fs               1.6.310            maven     12    x    x      x      x      -         -      .  .  x  .  .  .  .  .  .  ."
            "  com.github.liquidz/antq      2.5.1095           maven     50    x    x      x      x      -         -      x  .  .  .  .  .  .  .  .  ."
            "  djblue/portal                0.42.1             maven  1,809    -    x      x      x      -         -      .  .  .  .  .  .  x  .  .  ."
            "  metosin/malli                0.11.0             maven     85    x    x      x      x      -         -      .  .  .  .  .  .  .  x  .  ."
            "  mount/mount                  0.1.17             maven      8    -    -      -      x      -         -      .  .  .  .  .  .  .  .  .  ."
            "  mvxcvi/puget                 1.3.4              maven     15    x    x      x      x      -         -      .  .  .  .  .  .  .  .  .  x"
            "  org.clojure/clojure          1.11.1             maven  4,008    x    x      x      x      -         -      .  .  .  .  .  .  .  .  .  ."
            "  org.clojure/tools.deps       0.18.1354          maven     58    x    x      x      x      -         -      .  x  x  .  .  .  .  .  x  ."
            "  org.jline/jline              3.21.0     3.23.0  maven    971    -    x      x      x      -         -      .  .  .  .  .  x  .  .  .  ."
            "  org.slf4j/slf4j-nop          2.0.7              maven      4    -    x      x      x      -         -      .  .  .  .  .  .  .  .  .  ."
            "  pjstadig/humane-test-output  0.11.0             maven      7    -    t      -      -      -         -      .  .  .  .  .  .  .  .  .  ."
            "  rewrite-clj/rewrite-clj      1.1.47             maven     73    -    -      -      x      -         -      .  .  .  .  .  .  .  .  .  ."
            "  zprint/zprint                1.2.7              maven    210    -    x      x      x      -         -      .  .  .  .  x  .  .  .  .  ."])))

(deftest polylith-workspace-ifc-deps-table
  (is (= (ws-ifc-deps-table/table (workspace))
         ["                                                                                              t                              "
          "                                                                                              e                              "
          "                                                                                              s                              "
          "                                                                                              t                              "
          "                                                                                           t  -                              "
          "                                                                                           e  r                              "
          "                                                                                           s  u                              "
          "                                                                                           t  n                              "
          "                                                                                           -  n                              "
          "                                                                                           r  e                              "
          "                                                                                           u  r                              "
          "                                        c                    i                             n  -                       w      "
          "                                        o                    m                             n  o                       o      "
          "                                        n                    a           p              t  e  r     u                 r  w   "
          "                                        f                    g           a              e  r  c  t  s  u              k  s   "
          "                                        i                    e           t              s  -  h  e  e  s     v     w  s  -   "
          "                                        g                    -     m  o  h              t  c  e  x  r  e     a     o  p  e   "
          "                                  c     -  c                 c     i  v  -              -  o  s  t  -  r     l  v  r  a  x  w"
          "                               c  o  c  r  r                 r     g  e  f        s     h  n  t  -  c  -     i  e  k  c  p  s"
          "                               h  m  o  e  e                 e     r  r  i     s  y     e  t  r  t  o  i     d  r  s  e  l  -"
          "                            a  a  m  m  a  a  d     f     h  a     a  v  n     h  s     l  r  a  a  n  n  u  a  s  p  -  o  f"
          "                            n  n  a  m  d  t  e  d  i  g  e  t  l  t  i  d     e  t  t  p  a  t  b  f  p  t  t  i  a  c  r  i"
          "                            t  g  n  o  e  o  p  o  l  i  l  o  i  o  e  e  s  l  e  a  e  c  o  l  i  u  i  o  o  c  l  e  l"
          "  brick                     q  e  d  n  r  r  s  c  e  t  p  r  b  r  w  r  h  l  m  p  r  t  r  e  g  t  l  r  n  e  j  r  e"
          "  ---------------------------------------------------------------------------------------------------------------------------"
          "  antq                      .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  api                       .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  x  x  x  ."
          "  change                    .  .  .  x  .  .  .  .  .  x  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  clojure-test-test-runner  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  ."
          "  command                   .  x  .  x  x  x  x  x  x  x  x  .  x  x  x  .  .  x  .  x  .  .  x  .  x  .  x  x  x  x  x  x  x"
          "  common                    .  .  .  .  .  .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  .  .  .  x  x  .  x  .  x  .  .  .  ."
          "  config-reader             .  .  .  x  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  ."
          "  creator                   .  .  .  x  .  .  .  .  x  x  .  .  .  .  .  .  .  .  .  .  t  .  .  .  .  .  x  .  .  .  .  .  ."
          "  deps                      .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  x  .  .  .  .  .  ."
          "  doc                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  ."
          "  file                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  git                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  help                      .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  x  .  x  .  .  .  ."
          "  image-creator             .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  image-creator-x           .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  lib                       x  .  .  x  x  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  t  .  .  x  x  .  x  .  .  .  .  .  ."
          "  migrator                  .  .  .  x  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  overview                  .  x  .  x  .  .  x  .  .  .  .  x  x  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  x  x  .  ."
          "  path-finder               .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  sh                        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  shell                     .  .  .  x  x  .  .  x  x  .  .  .  .  .  .  .  x  .  x  x  .  .  .  .  x  x  x  .  .  .  .  x  ."
          "  system                    .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  system-x                  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  tap                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  test-helper               .  .  x  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  .  .  .  .  .  .  ."
          "  test-runner-contract      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  test-runner-orchestrator  .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  x  .  .  .  .  ."
          "  text-table                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  user-config               .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  user-input                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  util                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  validator                 .  .  .  x  .  .  x  .  .  .  .  .  .  .  .  x  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  ."
          "  version                   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
          "  workspace                 .  .  .  x  .  .  x  .  x  .  .  .  .  .  .  x  .  .  .  .  .  .  .  x  .  .  x  x  .  .  .  .  ."
          "  workspace-clj             .  .  .  x  x  .  x  .  x  x  .  .  x  .  .  x  .  .  .  .  .  .  .  .  x  .  x  .  x  .  .  .  ."
          "  ws-explorer               .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  ws-file                   .  .  .  x  .  .  .  .  x  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  x  .  .  .  ."
          "  nav-generator             .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
          "  poly-cli                  .  .  x  .  t  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  x  t  .  .  t  .  ."])))

(deftest polylith-workspace-project-deps-table
  (let [ws (workspace)
        projects (:projects ws)
        project (common/find-project "poly" projects)]
    (is (= (ws-project-deps-table/table (workspace) project)
           ["                                                                                              t                              "
            "                                                                                              e                              "
            "                                                                                              s                              "
            "                                                                                              t                              "
            "                                                                                           t  -                              "
            "                                                                                           e  r                              "
            "                                                                                           s  u                              "
            "                                                                                           t  n                              "
            "                                                                                           -  n                              "
            "                                                                                           r  e                              "
            "                                                                                           u  r                              "
            "                                        c                    i                             n  -                       w      "
            "                                        o                    m                             n  o                       o      "
            "                                        n                    a           p              t  e  r     u                 r  w   "
            "                                        f                    g           a              e  r  c  t  s  u              k  s   "
            "                                        i                    e           t              s  -  h  e  e  s     v     w  s  -   "
            "                                        g                    -     m  o  h              t  c  e  x  r  e     a     o  p  e   "
            "                                  c     -  c                 c     i  v  -              -  o  s  t  -  r     l  v  r  a  x  w"
            "                               c  o  c  r  r                 r     g  e  f        s     h  n  t  -  c  -     i  e  k  c  p  s"
            "                               h  m  o  e  e                 e     r  r  i     s  y     e  t  r  t  o  i     d  r  s  e  l  -"
            "                            a  a  m  m  a  a  d     f     h  a     a  v  n     h  s     l  r  a  a  n  n  u  a  s  p  -  o  f"
            "                            n  n  a  m  d  t  e  d  i  g  e  t  l  t  i  d     e  t  t  p  a  t  b  f  p  t  t  i  a  c  r  i"
            "                            t  g  n  o  e  o  p  o  l  i  l  o  i  o  e  e  s  l  e  a  e  c  o  l  i  u  i  o  o  c  l  e  l"
            "  brick                     q  e  d  n  r  r  s  c  e  t  p  r  b  r  w  r  h  l  m  p  r  t  r  e  g  t  l  r  n  e  j  r  e"
            "  ---------------------------------------------------------------------------------------------------------------------------"
            "  antq                      .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  .  +  .  .  .  .  +  +  .  +  .  +  .  .  .  ."
            "  api                       +  x  .  +  +  .  +  .  +  +  .  +  +  .  .  +  +  .  +  .  .  +  .  +  +  x  +  +  +  x  x  x  ."
            "  change                    .  .  .  x  .  .  .  .  +  x  .  +  .  .  .  x  +  .  +  .  .  .  .  +  +  .  x  .  +  .  .  .  ."
            "  clojure-test-test-runner  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  ."
            "  command                   +  x  .  x  x  x  x  x  x  x  x  +  x  x  x  +  +  x  +  x  .  +  x  +  x  +  x  x  x  x  x  x  x"
            "  common                    .  .  .  .  .  .  .  .  x  .  .  x  .  .  .  .  .  .  +  .  .  .  .  x  x  .  x  .  x  .  .  .  ."
            "  config-reader             .  .  .  x  .  .  +  .  x  .  .  +  .  .  .  +  .  .  +  .  .  +  .  +  +  .  x  x  +  .  .  .  ."
            "  creator                   -  -  -  x  -  -  -  -  x  x  -  +  -  -  -  -  +  -  +  -  t  -  -  +  +  -  x  -  +  -  -  -  -"
            "  deps                      .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  .  +  .  .  .  .  x  x  .  x  .  +  .  .  .  ."
            "  doc                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  +  .  .  .  .  .  .  .  .  .  x  .  .  .  ."
            "  file                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  git                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  help                      .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  .  x  .  .  .  .  +  +  .  x  .  x  .  .  .  ."
            "  image-creator             .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  lib                       x  -  -  x  x  -  +  -  x  -  -  +  -  -  -  +  -  -  +  -  t  +  -  x  x  -  x  +  +  -  -  -  -"
            "  migrator                  .  .  .  x  x  .  +  .  +  .  .  +  .  .  .  +  .  .  +  .  .  +  .  +  +  .  +  +  +  .  .  .  ."
            "  overview                  +  x  .  x  +  .  x  .  +  +  .  x  x  .  .  +  +  .  +  .  .  +  .  +  +  x  x  +  +  x  x  .  ."
            "  path-finder               .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  sh                        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  shell                     .  .  .  x  x  .  +  x  x  .  .  +  .  .  .  +  x  .  x  x  .  +  .  +  x  x  x  +  +  .  .  x  ."
            "  system                    .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  tap                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  test-helper               -  -  t  -  -  -  -  -  t  -  -  -  -  -  -  -  -  -  -  -  .  -  -  -  t  t  -  -  -  -  -  -  -"
            "  test-runner-contract      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  test-runner-orchestrator  .  .  .  x  .  .  x  .  +  .  .  +  .  .  .  +  .  .  +  .  .  x  .  +  +  .  x  x  +  .  .  .  ."
            "  text-table                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  user-config               .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  user-input                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  util                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  validator                 .  .  .  x  .  .  x  .  +  .  .  +  .  .  .  x  .  .  +  .  .  x  .  +  +  .  x  .  +  .  .  .  ."
            "  version                   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  workspace                 .  .  .  x  .  .  x  .  x  .  .  +  .  .  .  x  .  .  +  .  .  +  .  x  +  .  x  x  +  .  .  .  ."
            "  workspace-clj             +  .  .  x  x  .  x  .  x  x  .  +  x  .  .  x  +  .  +  .  .  +  .  +  x  .  x  +  x  .  .  .  ."
            "  ws-explorer               .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  ws-file                   .  .  .  x  .  .  .  .  x  x  .  +  .  .  .  .  +  .  +  .  .  .  .  +  +  .  x  .  x  .  .  .  ."
            "  nav-generator             .  .  .  +  x  .  +  .  +  .  .  +  .  .  .  +  .  .  +  .  .  +  .  +  +  .  x  +  +  .  .  .  ."
            "  poly-cli                  +  +  x  +  t  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  .  +  +  +  +  x  x  t  +  +  t  +  +"]))))

(deftest polylith-workspace-project-deps-table-indirect
  (let [ws (workspace)
        projects (:projects ws)
        project (common/find-project "poly" projects)]
    (is (= (ws-project-deps-table/table (workspace) project)
           ["                                                                                              t                              "
            "                                                                                              e                              "
            "                                                                                              s                              "
            "                                                                                              t                              "
            "                                                                                           t  -                              "
            "                                                                                           e  r                              "
            "                                                                                           s  u                              "
            "                                                                                           t  n                              "
            "                                                                                           -  n                              "
            "                                                                                           r  e                              "
            "                                                                                           u  r                              "
            "                                        c                    i                             n  -                       w      "
            "                                        o                    m                             n  o                       o      "
            "                                        n                    a           p              t  e  r     u                 r  w   "
            "                                        f                    g           a              e  r  c  t  s  u              k  s   "
            "                                        i                    e           t              s  -  h  e  e  s     v     w  s  -   "
            "                                        g                    -     m  o  h              t  c  e  x  r  e     a     o  p  e   "
            "                                  c     -  c                 c     i  v  -              -  o  s  t  -  r     l  v  r  a  x  w"
            "                               c  o  c  r  r                 r     g  e  f        s     h  n  t  -  c  -     i  e  k  c  p  s"
            "                               h  m  o  e  e                 e     r  r  i     s  y     e  t  r  t  o  i     d  r  s  e  l  -"
            "                            a  a  m  m  a  a  d     f     h  a     a  v  n     h  s     l  r  a  a  n  n  u  a  s  p  -  o  f"
            "                            n  n  a  m  d  t  e  d  i  g  e  t  l  t  i  d     e  t  t  p  a  t  b  f  p  t  t  i  a  c  r  i"
            "                            t  g  n  o  e  o  p  o  l  i  l  o  i  o  e  e  s  l  e  a  e  c  o  l  i  u  i  o  o  c  l  e  l"
            "  brick                     q  e  d  n  r  r  s  c  e  t  p  r  b  r  w  r  h  l  m  p  r  t  r  e  g  t  l  r  n  e  j  r  e"
            "  ---------------------------------------------------------------------------------------------------------------------------"
            "  antq                      .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  .  +  .  .  .  .  +  +  .  +  .  +  .  .  .  ."
            "  api                       +  x  .  +  +  .  +  .  +  +  .  +  +  .  .  +  +  .  +  .  .  +  .  +  +  x  +  +  +  x  x  x  ."
            "  change                    .  .  .  x  .  .  .  .  +  x  .  +  .  .  .  x  +  .  +  .  .  .  .  +  +  .  x  .  +  .  .  .  ."
            "  clojure-test-test-runner  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  x  .  .  .  .  .  ."
            "  command                   +  x  .  x  x  x  x  x  x  x  x  +  x  x  x  +  +  x  +  x  .  +  x  +  x  +  x  x  x  x  x  x  x"
            "  common                    .  .  .  .  .  .  .  .  x  .  .  x  .  .  .  .  .  .  +  .  .  .  .  x  x  .  x  .  x  .  .  .  ."
            "  config-reader             .  .  .  x  .  .  +  .  x  .  .  +  .  .  .  +  .  .  +  .  .  +  .  +  +  .  x  x  +  .  .  .  ."
            "  creator                   -  -  -  x  -  -  -  -  x  x  -  +  -  -  -  -  +  -  +  -  t  -  -  +  +  -  x  -  +  -  -  -  -"
            "  deps                      .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  .  +  .  .  .  .  x  x  .  x  .  +  .  .  .  ."
            "  doc                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  +  .  .  .  .  .  .  .  .  .  x  .  .  .  ."
            "  file                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  git                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  help                      .  .  .  x  .  .  .  .  +  .  .  +  .  .  .  .  .  .  x  .  .  .  .  +  +  .  x  .  x  .  .  .  ."
            "  image-creator             .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  lib                       x  -  -  x  x  -  +  -  x  -  -  +  -  -  -  +  -  -  +  -  t  +  -  x  x  -  x  +  +  -  -  -  -"
            "  migrator                  .  .  .  x  x  .  +  .  +  .  .  +  .  .  .  +  .  .  +  .  .  +  .  +  +  .  +  +  +  .  .  .  ."
            "  overview                  +  x  .  x  +  .  x  .  +  +  .  x  x  .  .  +  +  .  +  .  .  +  .  +  +  x  x  +  +  x  x  .  ."
            "  path-finder               .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  sh                        .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  shell                     .  .  .  x  x  .  +  x  x  .  .  +  .  .  .  +  x  .  x  x  .  +  .  +  x  x  x  +  +  .  .  x  ."
            "  system                    .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  tap                       .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  test-helper               -  -  t  -  -  -  -  -  t  -  -  -  -  -  -  -  -  -  -  -  .  -  -  -  t  t  -  -  -  -  -  -  -"
            "  test-runner-contract      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  test-runner-orchestrator  .  .  .  x  .  .  x  .  +  .  .  +  .  .  .  +  .  .  +  .  .  x  .  +  +  .  x  x  +  .  .  .  ."
            "  text-table                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  user-config               .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  user-input                .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  util                      .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  validator                 .  .  .  x  .  .  x  .  +  .  .  +  .  .  .  x  .  .  +  .  .  x  .  +  +  .  x  .  +  .  .  .  ."
            "  version                   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  .  .  .  .  .  .  .  .  ."
            "  workspace                 .  .  .  x  .  .  x  .  x  .  .  +  .  .  .  x  .  .  +  .  .  +  .  x  +  .  x  x  +  .  .  .  ."
            "  workspace-clj             +  .  .  x  x  .  x  .  x  x  .  +  x  .  .  x  +  .  +  .  .  +  .  +  x  .  x  +  x  .  .  .  ."
            "  ws-explorer               .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  x  .  .  .  .  .  ."
            "  ws-file                   .  .  .  x  .  .  .  .  x  x  .  +  .  .  .  .  +  .  +  .  .  .  .  +  +  .  x  .  x  .  .  .  ."
            "  nav-generator             .  .  .  +  x  .  +  .  +  .  .  +  .  .  .  +  .  .  +  .  .  +  .  +  +  .  x  +  +  .  .  .  ."
            "  poly-cli                  +  +  x  +  t  +  +  +  +  +  +  +  +  +  +  +  +  +  +  +  .  +  +  +  +  x  x  t  +  +  t  +  +"]))))

(deftest polylith-brick-and-project-deps
  (let [{:keys [components projects] :as ws} (workspace)
        project (common/find-project "poly" projects)
        brick (common/find-component "workspace" components)]
    (is (= (brick-deps-table/table ws project brick "none")
           ["  used by   <  workspace  >  uses       "
            "  --------                   -----------"
            "  api                        common     "
            "  command                    deps       "
            "  overview                   file       "
            "                             path-finder"
            "                             text-table "
            "                             util       "
            "                             validator  "]))))

(deftest polylith-project-brick-deps
  (let [{:keys [components] :as ws} (workspace)
        brick (common/find-component "workspace" components)]
    (is (= (brick-ifc-deps/table ws brick)
           ["  used by   <  workspace  >  uses       "
            "  --------                   -----------"
            "  api                        common     "
            "  command                    deps       "
            "  overview                   file       "
            "                             path-finder"
            "                             text-table "
            "                             util       "
            "                             validator  "]))))

(deftest polylith-poly-project-deps
  (is (= (ws-explorer/extract (workspace) ["projects" "poly" "deps"])
         {"antq"                     {:src  {:direct   ["common"]
                                             :indirect ["file"
                                                        "image-creator"
                                                        "system"
                                                        "text-table"
                                                        "user-config"
                                                        "util"
                                                        "version"]}
                                      :test {}}
          "api"                      {:src  {:direct   ["change"
                                                        "user-input"
                                                        "workspace"
                                                        "workspace-clj"
                                                        "ws-explorer"]
                                             :indirect ["antq"
                                                        "common"
                                                        "config-reader"
                                                        "deps"
                                                        "file"
                                                        "git"
                                                        "image-creator"
                                                        "lib"
                                                        "path-finder"
                                                        "sh"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-config"
                                                        "util"
                                                        "validator"
                                                        "version"]}
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
          "clojure-test-test-runner" {:src  {:direct ["test-runner-contract"
                                                      "util"]}
                                      :test {:direct ["test-runner-contract"
                                                      "util"]}}
          "command"                  {:src  {:direct   ["change"
                                                        "common"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "file"
                                                        "git"
                                                        "help"
                                                        "lib"
                                                        "migrator"
                                                        "overview"
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
                                             :indirect ["antq"
                                                        "image-creator"
                                                        "path-finder"
                                                        "sh"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-input"]}
                                      :test {:direct   ["change"
                                                        "common"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "file"
                                                        "git"
                                                        "help"
                                                        "lib"
                                                        "migrator"
                                                        "overview"
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
                                             :indirect ["antq"
                                                        "image-creator"
                                                        "path-finder"
                                                        "sh"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-input"]}}
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
                                                        "path-finder"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-config"
                                                        "version"]}}
          "creator"                  {:src  {:direct   ["common"
                                                        "file"
                                                        "git"
                                                        "util"]
                                             :indirect ["image-creator"
                                                        "sh"
                                                        "system"
                                                        "text-table"
                                                        "user-config"
                                                        "version"]}
                                      :test {:direct   ["common"
                                                        "file"
                                                        "git"
                                                        "test-helper"
                                                        "util"]
                                             :indirect ["antq"
                                                        "change"
                                                        "command"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "help"
                                                        "image-creator"
                                                        "lib"
                                                        "migrator"
                                                        "overview"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "system"
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
          "image-creator"            {:src  {:direct ["file"
                                                      "util"]}
                                      :test {}}
          "lib"                      {:src  {:direct   ["antq"
                                                        "common"
                                                        "config-reader"
                                                        "file"
                                                        "text-table"
                                                        "user-config"
                                                        "util"]
                                             :indirect ["deps"
                                                        "image-creator"
                                                        "path-finder"
                                                        "system"
                                                        "test-runner-contract"
                                                        "validator"
                                                        "version"]}
                                      :test {:direct   ["antq"
                                                        "common"
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
                                                        "doc"
                                                        "git"
                                                        "help"
                                                        "image-creator"
                                                        "lib"
                                                        "migrator"
                                                        "overview"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "system"
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
                                                        "image-creator"
                                                        "path-finder"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "user-config"
                                                        "util"
                                                        "validator"
                                                        "version"]}
                                      :test {}}
          "nav-generator"            {:src  {:direct   ["config-reader"
                                                        "util"]
                                             :indirect ["common"
                                                        "deps"
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
          "overview"                 {:src  {:direct   ["change"
                                                        "common"
                                                        "deps"
                                                        "image-creator"
                                                        "lib"
                                                        "user-input"
                                                        "util"
                                                        "workspace"
                                                        "workspace-clj"]
                                             :indirect ["antq"
                                                        "config-reader"
                                                        "file"
                                                        "git"
                                                        "path-finder"
                                                        "sh"
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
                                                        "common"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "file"
                                                        "git"
                                                        "help"
                                                        "image-creator"
                                                        "lib"
                                                        "migrator"
                                                        "overview"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "system"
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
                                             :indirect ["antq"
                                                        "change"
                                                        "common"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "file"
                                                        "git"
                                                        "help"
                                                        "image-creator"
                                                        "lib"
                                                        "migrator"
                                                        "overview"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "system"
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
                                                        "doc"
                                                        "file"
                                                        "sh"
                                                        "system"
                                                        "tap"
                                                        "user-config"
                                                        "user-input"
                                                        "util"
                                                        "ws-explorer"]
                                             :indirect ["deps"
                                                        "image-creator"
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "validator"
                                                        "version"]}
                                      :test {:direct   ["common"
                                                        "config-reader"
                                                        "doc"
                                                        "file"
                                                        "sh"
                                                        "system"
                                                        "tap"
                                                        "user-config"
                                                        "user-input"
                                                        "util"
                                                        "ws-explorer"]
                                             :indirect ["deps"
                                                        "image-creator"
                                                        "path-finder"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "validator"
                                                        "version"]}}
          "system"                   {:src  {}
                                      :test {}}
          "tap"                      {:src  {}
                                      :test {}}
          "test-helper"              {:src  {}
                                      :test {:direct   ["command"
                                                        "file"
                                                        "user-config"
                                                        "user-input"]
                                             :indirect ["antq"
                                                        "change"
                                                        "common"
                                                        "config-reader"
                                                        "creator"
                                                        "deps"
                                                        "doc"
                                                        "git"
                                                        "help"
                                                        "image-creator"
                                                        "lib"
                                                        "migrator"
                                                        "overview"
                                                        "path-finder"
                                                        "sh"
                                                        "shell"
                                                        "system"
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
                                                        "deps"
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
                                                        "deps"
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
          "workspace"                {:src  {:direct   ["common"
                                                        "deps"
                                                        "file"
                                                        "path-finder"
                                                        "text-table"
                                                        "util"
                                                        "validator"]
                                             :indirect ["image-creator"
                                                        "system"
                                                        "test-runner-contract"
                                                        "user-config"
                                                        "version"]}
                                      :test {:direct   ["common"
                                                        "deps"
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
          "workspace-clj"            {:src  {:direct   ["common"
                                                        "config-reader"
                                                        "deps"
                                                        "file"
                                                        "git"
                                                        "lib"
                                                        "path-finder"
                                                        "user-config"
                                                        "util"
                                                        "version"]
                                             :indirect ["antq"
                                                        "image-creator"
                                                        "sh"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "validator"]}
                                      :test {:direct   ["common"
                                                        "config-reader"
                                                        "deps"
                                                        "file"
                                                        "git"
                                                        "lib"
                                                        "path-finder"
                                                        "user-config"
                                                        "util"
                                                        "version"]
                                             :indirect ["antq"
                                                        "image-creator"
                                                        "sh"
                                                        "system"
                                                        "test-runner-contract"
                                                        "text-table"
                                                        "validator"]}}
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
                                      :test {}}})))

(deftest polylith-poly-project-src-paths
  (is (= (ws-explorer/extract (workspace) ["projects" "poly" "paths" "src"])
         ["bases/nav-generator/src"
          "bases/poly-cli/src"
          "components/antq/src"
          "components/api/src"
          "components/change/src"
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
          "components/lib/src"
          "components/migrator/src"
          "components/overview/src"
          "components/path-finder/src"
          "components/sh/src"
          "components/shell/src"
          "components/system/src"
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

(deftest polylith-poly-project-test-paths
  (is (= (ws-explorer/extract (workspace) ["projects" "poly" "paths" "test"])
         ["bases/poly-cli/test"
          "components/api/test"
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

(deftest polylith-poly-project-lib-imports
  (is (= (ws-explorer/extract (workspace) ["projects" "poly" "lib-imports"])
         {:src  ["antq.api"
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
                 "org.eclipse.aether.util.version"
                 "org.jline.reader"
                 "org.jline.reader.impl"
                 "org.jline.terminal"
                 "polylith.clj.core.nav-generator.help-generator"
                 "polylith.clj.core.nav-generator.pages-generator"
                 "polylith.clj.core.nav-generator.ws-generator"
                 "portal.api"
                 "puget.printer"
                 "zprint.core"]
          :test ["clojure.lang"
                 "clojure.test"
                 "malli.core"
                 "polylith.clj.core.poly-cli.api"
                 "polylith.clj.core.poly-cli.core"]})))

(deftest polylith-shell-component-lib-deps
  (is (= (ws-explorer/extract (workspace) ["components" "shell" "lib-deps"])
         {:src {"org.jline/jline" {:size    994664
                                   :type    "maven"
                                   :version "3.21.0"}}})))

(deftest profile-info
  (is (= (run-cmd "examples/profiles"
                  "info"
                  ":no-changes")
         ["  stable since: 1234567                       "
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
          "  calculator   calculator1    ---   s--   --  "
          "  database     database1      st-   st-   --  "
          "  test-helper  test-helper1   -t-   -t-   --  "
          "  user         admin          ---   ---   st  "
          "  user         user1          st-   st-   --  "
          "  util         util1          st-   st-   --  "
          "  -            base1          st-   st-   --  "
          "  -            base2          st-   st-   --  "
          ""
          "  Error 107: Missing components in the service project for these interfaces: calculator"])))

(deftest profile-info-where-test-has-changed
  (is (= (run-cmd "examples/profiles"
                  "info"
                  "changed-files:bases/base2/test/se/example/base2/core_test.clj")
         ["  stable since: 1234567                       "
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
          "  calculator   calculator1    ---   s--   --  "
          "  database     database1      st-   st-   --  "
          "  test-helper  test-helper1   -t-   -t-   --  "
          "  user         admin          ---   ---   st  "
          "  user         user1          st-   st-   --  "
          "  util         util1          st-   st-   --  "
          "  -            base1          stx   st-   --  "
          "  -            base2 *        stx   st-   --  "
          ""
          "  Error 107: Missing components in the service project for these interfaces: calculator"])))

(deftest profile-info-where-src-has-changed
  (is (= (run-cmd "examples/profiles"
                  "info"
                  "changed-files:bases/base2/src/se/example/base2/core.clj")
         ["  stable since: 1234567                       "
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
          "  calculator   calculator1    ---   s--   --  "
          "  database     database1      st-   st-   --  "
          "  test-helper  test-helper1   -t-   -t-   --  "
          "  user         admin          ---   ---   st  "
          "  user         user1          st-   st-   --  "
          "  util         util1          st-   st-   --  "
          "  -            base1          stx   st-   --  "
          "  -            base2 *        stx   st-   --  "
          ""
          "  Error 107: Missing components in the service project for these interfaces: calculator"])))

(deftest profile-info-loc
  (is (= (run-cmd "examples/profiles"
                  "info" ":loc"
                  ":no-changes")
         ["  stable since: 1234567                                 "
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
          "  calculator   calculator1    ---   s--   --       1   0"
          "  database     database1      st-   st-   --       3   6"
          "  test-helper  test-helper1   -t-   -t-   --       3   3"
          "  user         admin          ---   ---   st       3   6"
          "  user         user1          st-   st-   --       3   5"
          "  util         util1          st-   st-   --       1   6"
          "  -            base1          st-   st-   --       1   7"
          "  -            base2          st-   st-   --       1   5"
          "                              12    13            16  38"
          ""
          "  Error 107: Missing components in the service project for these interfaces: calculator"])))

(deftest profile-info-skip-dev
  (is (= (run-cmd "examples/profiles"
                  "info" "skip:dev"
                  ":no-changes")
         ["  stable since: 1234567          "
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
          "  calculator   calculator1    ---"
          "  database     database1      st-"
          "  test-helper  test-helper1   -t-"
          "  user         admin          ---"
          "  user         user1          st-"
          "  util         util1          st-"
          "  -            base1          st-"
          "  -            base2          st-"
          ""
          "  Error 107: Missing components in the service project for these interfaces: calculator"])))

(deftest profile-deps
  (is (= (run-cmd "examples/profiles"
                  "deps")
         ["                      t      "
          "                c     e      "
          "                a     s      "
          "                l  d  t      "
          "                c  a  -      "
          "                u  t  h      "
          "                l  a  e     b"
          "                a  b  l  u  a"
          "                t  a  p  t  s"
          "                o  s  e  i  e"
          "  brick         r  e  r  l  2"
          "  ---------------------------"
          "  admin         .  .  .  .  ."
          "  calculator1   .  .  .  .  ."
          "  database1     x  .  .  x  ."
          "  test-helper1  .  .  .  .  ."
          "  user1         .  t  t  .  ."
          "  util1         .  .  .  .  ."
          "  base1         .  .  .  .  t"
          "  base2         .  .  .  .  ."])))

(deftest profile-deps-project
  (is (= (run-cmd "examples/profiles"
                  "deps" "project:s")
         ["                      t      "
          "                      e      "
          "                c     s      "
          "                a  d  t      "
          "                l  a  -      "
          "                c  t  h      "
          "                u  a  e      "
          "                l  b  l  u  b"
          "                a  a  p  t  a"
          "                t  s  e  i  s"
          "                o  e  r  l  e"
          "  brick         r  1  1  1  2"
          "  ---------------------------"
          "  database1     x  .  .  x  ."
          "  test-helper1  .  .  .  .  ."
          "  user1         .  t  t  -  ."
          "  util1         .  .  .  .  ."
          "  base1         .  .  .  .  t"
          "  base2         .  .  .  .  ."])))

(deftest profile-deps-brick
  (is (= (run-cmd "examples/profiles"
                  "deps" "brick:database1")
         ["  used by    <  database1  >  uses      "
          "  ---------                   ----------"
          "  user1 (t)                   calculator"
          "                              util      "])))

(deftest profile-deps-project-brick
  (is (= (run-cmd "examples/profiles"
                  "deps" "project:s" "brick:database1")
         ["  used by    <  database1  >  uses      "
          "  ---------                   ----------"
          "  user1 (t)                   calculator"
          "                              util1     "])))

(deftest profile-libs
  (is (= (run-cmd "examples/profiles"
                  "libs")
         ["                                                                         t"
          "                                                                         e"
          "                                                                         s"
          "                                                                         t"
          "                                                                         -"
          "                                                                         h"
          "                                                                         e"
          "                                                                         l"
          "                                                                         p"
          "                                                                         e"
          "                                                                         r"
          "  library              version  type      KB   s   dev  default  extra   1"
          "  ------------------------------------------   -   -------------------   -"
          "  clj-commons/fs       1.6.310  maven     12   -    -      x       -     ."
          "  metosin/malli        0.11.1   maven      -   t    x      -       -     x"
          "  org.clojure/clojure  1.11.1   maven  4,008   x    x      -       -     ."])))

(deftest profile-libs-skip-dev
  (is (= (run-cmd "examples/profiles"
                  "libs" "skip:dev")
         ["                                                   t"
          "                                                   e"
          "                                                   s"
          "                                                   t"
          "                                                   -"
          "                                                   h"
          "                                                   e"
          "                                                   l"
          "                                                   p"
          "                                                   e"
          "                                                   r"
          "  library              version  type      KB   s   1"
          "  ------------------------------------------   -   -"
          "  clj-commons/fs       1.6.310  maven     12   -   ."
          "  metosin/malli        0.11.1   maven      -   t   x"
          "  org.clojure/clojure  1.11.1   maven  4,008   x   ."])))

(defn clean-settings [ws]
  (let [vcs (dissoc (:vcs ws) :branch :stable-since)]
    (dissoc (assoc ws :vcs vcs)
            :user-config-filename
            :user-home)))

(deftest ws-get-settings
  (let [actual (clean-settings (ws-get "."
                                       [:settings]
                                       "ws-dir:examples/profiles"))]
    (is (= actual
           {:active-profiles #{"default"}
            :color-mode "none"
            :compact-views #{}
            :default-profile-name "default"
            :empty-character "."
            :interface-ns "interface"
            :m2-dir (str (System/getProperty "user.home") "/.m2")
            :profile-to-settings  {"default" {:base-names []
                                              :component-names ["user1"]
                                              :lib-deps {"clj-commons/fs" {:size 12819
                                                                           :type "maven"
                                                                           :version "1.6.310"}}
                                              :paths ["components/user1/src"
                                                      "components/user1/test"]
                                              :project-names []}
                                   "extra" {:base-names []
                                            :component-names ["admin"]
                                            :lib-deps {}
                                            :paths ["components/admin/src"
                                                    "components/admin/test"]
                                            :project-names []}}
            :projects {"development" {:alias "dev"
                                      :test {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                       "service" {:alias "s"
                                  :necessary ["user1"]
                                  :test {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}}
            :tag-patterns {:release "v[0-9]*"
                           :stable "stable-*"}
            :thousand-separator ","
            :top-namespace "se.example"
            :vcs {:git-root (System/getProperty "user.dir")
                  :name "git"
                  :auto-add true
                  :is-git-repo true
                  :polylith {:branch "master"
                             :repo "https://github.com/polyfy/polylith.git"}}}))))
