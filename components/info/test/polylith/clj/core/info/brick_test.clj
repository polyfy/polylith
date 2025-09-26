(ns polylith.clj.core.info.brick-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.info.table.brick :as brick]
            [polylith.clj.core.info.workspace-data :as data]))

(deftest ws-table--clean
  (with-redefs [file/exists (fn [_] true)]
    (is (= ["  interface      brick           poly  core   dev"
            "  ----------------------------   ----------   ---"
            "  change         change          st-   s--    st-"
            "  command        command         stx   ---    st-"
            "  common         common          st-   s--    st-"
            "  creator        creator         st-   ---    ---"
            "  deps           deps            st-   s--    st-"
            "  file           file            st-   s--    ---"
            "  git            git             st-   s--    st-"
            "  help           help            st-   s--    st-"
            "  path-finder    path-finder *   stx   s--    st-"
            "  shell          shell           st-   s--    st-"
            "  test-helper    test-helper     st-   ---    st-"
            "  test-runner    test-runner     st-   ---    st-"
            "  text-table     text-table      st-   s--    st-"
            "  user-config    user-config     st-   s--    st-"
            "  util           util            st-   s--    st-"
            "  validator      validator       st-   s--    st-"
            "  workspace      workspace *     stx   s--    st-"
            "  workspace-clj  workspace-clj   st-   ---    st-"
            "  -              poly-cli        st-   ---    st-"]
           (brick/table data/workspace false false false)))))

(deftest ws-table--with-loc-and-resources
  (with-redefs [file/exists (fn [_] true)]
    (is (= ["  interface      brick           poly   core     dev      loc   (t)"
            "  ----------------------------   ------------   -----   -----------"
            "  change         change          s-t-   s---    s-t-      134   343"
            "  command        command         s-tx   ----    s-t-      151     0"
            "  common         common          s-t-   s---    s-t-      336    53"
            "  creator        creator         srt-   ----    ----      181   282"
            "  deps           deps            s-t-   s---    s-t-      242   328"
            "  file           file            s-t-   s---    ----      165     2"
            "  git            git             s-t-   s---    s-t-       55    18"
            "  help           help            s-t-   s---    s-t-      204     0"
            "  path-finder    path-finder *   s-tx   s---    s-t-      591   343"
            "  shell          shell           s-t-   s---    s-t-       19     0"
            "  test-helper    test-helper     s-t-   ----    s-t-       73     0"
            "  test-runner    test-runner     s-t-   ----    s-t-      108     0"
            "  text-table     text-table      s-t-   s---    s-t-      145   117"
            "  user-config    user-config     s-t-   s---    s-t-       18     0"
            "  util           util            s-t-   s---    s-t-      290    64"
            "  validator      validator       s-t-   s---    s-t-      420   810"
            "  workspace      workspace *     s-tx   s---    s-t-      844 1,008"
            "  workspace-clj  workspace-clj   s-t-   ----    s-t-      324   150"
            "  -              poly-cli        s-t-   ----    s-t-       22     0"
            "                                 4,322  3,463   3,976   4,322 3,518"]
           (brick/table data/workspace false true true)))))

(deftest ws-table--with-dialect
  (with-redefs [file/exists (fn [_] true)]
    (is (= ["  interface      brick          dialect   poly  core   dev"
            "  -------------------------------------   ----------   ---"
            "  change         change           j--     st-   s--    st-"
            "  command        command          j--     stx   ---    st-"
            "  common         common           j--     st-   s--    st-"
            "  creator        creator          jc-     st-   ---    ---"
            "  deps           deps             jcs     st-   s--    st-"
            "  file           file             j--     st-   s--    ---"
            "  git            git              j--     st-   s--    st-"
            "  help           help             j--     st-   s--    st-"
            "  path-finder    path-finder *    j--     stx   s--    st-"
            "  shell          shell            j--     st-   s--    st-"
            "  test-helper    test-helper      j--     st-   ---    st-"
            "  test-runner    test-runner      j--     st-   ---    st-"
            "  text-table     text-table       j--     st-   s--    st-"
            "  user-config    user-config      j--     st-   s--    st-"
            "  util           util             j--     st-   s--    st-"
            "  validator      validator        j--     st-   s--    st-"
            "  workspace      workspace *      j--     stx   s--    st-"
            "  workspace-clj  workspace-clj    j--     st-   ---    st-"
            "  -              poly-cli         j--     st-   ---    st-"]
           (brick/table data/workspace true false false)))))

(deftest ws-table--with-dialect-and-loc
  (with-redefs [file/exists (fn [_] true)]
    (is (= ["  interface      brick          dialect   poly   core     dev      loc   (t)"
            "  -------------------------------------   ------------   -----   -----------"
            "  change         change           j--      st-    s--     st-      134   343"
            "  command        command          j--      stx    ---     st-      151     0"
            "  common         common           j--      st-    s--     st-      336    53"
            "  creator        creator          jc-      st-    ---     ---      181   282"
            "  deps           deps             jcs      st-    s--     st-      242   328"
            "  file           file             j--      st-    s--     ---      165     2"
            "  git            git              j--      st-    s--     st-       55    18"
            "  help           help             j--      st-    s--     st-      204     0"
            "  path-finder    path-finder *    j--      stx    s--     st-      591   343"
            "  shell          shell            j--      st-    s--     st-       19     0"
            "  test-helper    test-helper      j--      st-    ---     st-       73     0"
            "  test-runner    test-runner      j--      st-    ---     st-      108     0"
            "  text-table     text-table       j--      st-    s--     st-      145   117"
            "  user-config    user-config      j--      st-    s--     st-       18     0"
            "  util           util             j--      st-    s--     st-      290    64"
            "  validator      validator        j--      st-    s--     st-      420   810"
            "  workspace      workspace *      j--      stx    s--     st-      844 1,008"
            "  workspace-clj  workspace-clj    j--      st-    ---     st-      324   150"
            "  -              poly-cli         j--      st-    ---     st-       22     0"
            "                                          4,322  3,463   3,976   4,322 3,518"]
           (brick/table data/workspace true true false)))))
