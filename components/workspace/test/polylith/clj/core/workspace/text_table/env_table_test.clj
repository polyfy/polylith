(ns polylith.clj.core.workspace.text-table.env-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.text-table.new-env-table :as env-table]))

(def workspace {:total-loc-src-environments 157
                :total-loc-test-environments 10
                :settings {:color-mode "none"
                           :thousand-sep ","}
                :environments [{:name "cli",
                                :alias "cli"
                                :dev? false
                                :has-src-dir? false
                                :has-test-dir? false
                                :lines-of-code-src 0
                                :lines-of-code-test 34}
                               {:name "core"
                                :alias "core"
                                :dev? false
                                :has-src-dir? false
                                :has-test-dir? false
                                :lines-of-code-src 10
                                :lines-of-code-test 1234}
                               {:name "development"
                                :alias "dev"
                                :dev? true
                                :has-src-dir? false
                                :has-test-dir? true
                                :lines-of-code-src 0
                                :lines-of-code-test 0}]
                :changes {:changed-environments ["cli"]
                          :environments-to-test ["development"]}})

(deftest table--environments-with-no-direct-change-but-with-tests-to-run--returns-correct-table
  (is (= ["  environment  alias  src"
          "  -----------------------"
          "  cli *        cli    ---"
          "  core         core   ---"
          "  development  dev    -xx"]
         (env-table/table workspace false))))

(deftest table--environments-with-loc--returns-table-with-lines-of-code
  (is (= ["  environment  alias  src  loc    (t)"
          "  -----------------------------------"
          "  cli *        cli    ---    0     34"
          "  core         core   ---   10  1,234"
          "  development  dev    -xx    0      0"
          "                           157     10"]
         (env-table/table workspace true))))
