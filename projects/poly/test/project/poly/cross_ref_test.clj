(ns project.poly.cross-ref-test
  (:require [clojure.test :refer :all]))

;; todo: move to poly-workspace-test

;(deftest the-test-context-of-br-depends-on-the-src-context-of-bm-but-is-not-affected-by-a-change-of-the-src-context-of-bm-because-only-the-test-context-of-bm-is-included-in-the-brp-project
;  (is (= (ws-get "examples/cross-ref"
;                 [:changes :project-to-bricks-to-test]
;                 "skip:dev"
;                 "changed-files:bases/bm/src/com/cross_ref/bm/core.clj")
;         {"bmp" ["bm"]
;          "brp" []})))
;
;(deftest trigger-tests-when-test-context-of-bm-is-changed-but-not-br-because-it-doesnt-depend-on-test-context-of-bm
;  (is (= (ws-get "examples/cross-ref"
;                 [:changes :project-to-bricks-to-test]
;                 "skip:dev"
;                 "changed-files:bases/bm/test/com/cross_ref/bm/core_test.clj")
;         {"bmp" ["bm"]
;          "brp" ["bm"]})))
;
;(deftest trigger-bm-test-when-config-file-has-changed
;  (is (= (ws-get "examples/cross-ref"
;                 [:changes :project-to-bricks-to-test]
;                 "skip:dev"
;                 "changed-files:bases/bm/deps.edn")
;         {"bmp" ["bm"]
;          "brp" []})))
;
;(deftest trigger-br-test-when-config-file-has-changed
;  (is (= (ws-get "examples/cross-ref"
;                 [:changes :project-to-bricks-to-test]
;                 "skip:dev"
;                 "changed-files:bases/br/deps.edn")
;         {"bmp" []
;          "brp" ["br"]})))
;
;(deftest when-no-changes-run-no-tests
;  (is (= (run-cmd "examples/cross-ref"
;                  "info" ":no-changes" "fake-sha:1234567")
;         ["  stable since: 1234567            "
;          "                                   "
;          "  projects: 3   interfaces: 0      "
;          "  bases:    2   components: 0      "
;          "                                   "
;          "  project      alias  source   dev "
;          "  --------------------------   --- "
;          "  bmp          bmp     ---     --- "
;          "  brp          brp     ---     --- "
;          "  development  dev     s--     s-- "
;          "                                   "
;          "  interface  brick   bmp  brp   dev"
;          "  ----------------   --------   ---"
;          "  -          bm      st-  -t-   st-"
;          "  -          br      ---  st-   st-"])))
;
;(deftest when-src-of-bm-has-changed-run-tests-for-bm-in-bmp-and-dev
;  (is (= (run-cmd "examples/cross-ref"
;                  "info" "fake-sha:1234567" ":dev"
;                  "changed-files:bases/bm/src/com/cross_ref/bm/core.clj")
;         ["  stable since: 1234567             "
;          "                                    "
;          "  projects: 3   interfaces: 0       "
;          "  bases:    2   components: 0       "
;          "                                    "
;          "  project        alias  source   dev"
;          "  ----------------------------   ---"
;          "  bmp +          bmp     ---     ---"
;          "  brp            brp     ---     ---"
;          "  development +  dev     s--     s--"
;          "                                    "
;          "  interface  brick   bmp  brp   dev "
;          "  ----------------   --------   --- "
;          "  -          bm *    stx  -t-   stx "
;          "  -          br      ---  st-   st- "])))
;
;(deftest when-test-of-bm-has-changed-run-tests-for-bm-in-all-projects
;  (is (= (run-cmd "examples/cross-ref"
;                  "info" "fake-sha:1234567" ":dev"
;                  "changed-files:bases/bm/test/com/cross_ref/bm/core_test.clj")
;         ["  stable since: 1234567             "
;          "                                    "
;          "  projects: 3   interfaces: 0       "
;          "  bases:    2   components: 0       "
;          "                                    "
;          "  project        alias  source   dev"
;          "  ----------------------------   ---"
;          "  bmp +          bmp     ---     ---"
;          "  brp +          brp     ---     ---"
;          "  development +  dev     s--     s--"
;          "                                    "
;          "  interface  brick   bmp  brp   dev "
;          "  ----------------   --------   --- "
;          "  -          bm *    stx  -tx   stx "
;          "  -          br      ---  st-   st- "])))
;
;(deftest when-src-of-br-has-changed-run-tests-for-br-in-brp-and-dev
;  (is (= (run-cmd "examples/cross-ref"
;                  "info" "fake-sha:1234567" ":dev"
;                  "changed-files:bases/br/src/com/cross_ref/br/core.clj")
;         ["  stable since: 1234567             "
;          "                                    "
;          "  projects: 3   interfaces: 0       "
;          "  bases:    2   components: 0       "
;          "                                    "
;          "  project        alias  source   dev"
;          "  ----------------------------   ---"
;          "  bmp            bmp     ---     ---"
;          "  brp +          brp     ---     ---"
;          "  development +  dev     s--     s--"
;          "                                    "
;          "  interface  brick   bmp  brp   dev "
;          "  ----------------   --------   --- "
;          "  -          bm      st-  -t-   st- "
;          "  -          br *    ---  stx   stx "])))
;
;(deftest when-test-of-br-has-changed-run-tests-for-br-in-brp-and-dev
;  (is (= (run-cmd "examples/cross-ref"
;                  "info" "fake-sha:1234567" ":dev"
;                  "changed-files:bases/br/test/com/cross_ref/br/core_test.clj")
;         ["  stable since: 1234567             "
;          "                                    "
;          "  projects: 3   interfaces: 0       "
;          "  bases:    2   components: 0       "
;          "                                    "
;          "  project        alias  source   dev"
;          "  ----------------------------   ---"
;          "  bmp            bmp     ---     ---"
;          "  brp +          brp     ---     ---"
;          "  development +  dev     s--     s--"
;          "                                    "
;          "  interface  brick   bmp  brp   dev "
;          "  ----------------   --------   --- "
;          "  -          bm      st-  -t-   st- "
;          "  -          br *    ---  stx   stx "])))