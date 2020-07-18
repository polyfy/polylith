(ns polylith.clj.core.workspace.text-table.env-table-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [polylith.clj.core.workspace.text-table.env-table :as text-table-env]))

(def environments [{:name "cli",
                    :alias "cli"
                    :has-src-dir? false
                    :has-test-dir? false
                    :lines-of-code-src 0
                    :lines-of-code-test 34}
                   {:name "core"
                    :alias "core"
                    :has-src-dir? false
                    :has-test-dir? false
                    :lines-of-code-src 10
                    :lines-of-code-test 1234}
                   {:name "development"
                    :alias "dev"
                    :has-src-dir? false
                    :has-test-dir? true
                    :lines-of-code-src 0
                    :lines-of-code-test 0}])

(def changes {:changed-environments []
              :environments-to-test ["development"]})

(deftest table--environments-with-no-direct-change-but-with-tests-to-run--returns-correct-table
  (is (= ["  environment  alias  src"
          "  -----------------------"
          "  cli          cli    ---"
          "  core         core   ---"
          "  development  dev    -xx"]
         (str/split-lines
           (text-table-env/table environments changes 0 0 "," false "none")))))

(deftest table--environments-with-loc--returns-table-with-lines-of-code
  (is (= ["  environment  alias  src  loc    (t)"
          "  -----------------------------------"
          "  cli          cli    ---    0     34"
          "  core         core   ---   10  1,234"
          "  development  dev    -xx    0      0"
          "                            10    157"]
         (str/split-lines
           (text-table-env/table environments changes 10 157 "," true "none")))))
