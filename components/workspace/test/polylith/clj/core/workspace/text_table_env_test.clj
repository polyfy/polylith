(ns polylith.clj.core.workspace.text-table-env-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [polylith.clj.core.workspace.text-table-env :as text-table-env]))

(def environments [{:name "cli",
                    :alias "cli"
                    :has-src-dir? false
                    :has-test-dir? false}
                   {:name "core"
                    :alias "core"
                    :has-src-dir? false
                    :has-test-dir? false}
                   {:name "development"
                    :alias "dev"
                    :has-src-dir? false
                    :has-test-dir? true}])

(def changes {:changed-environments []
              :environments-to-test ["dev"]})

(deftest table--environments-with-no-direct-change-but-with-indirect-changes--returns-correct-table
  (is (= ["  environment  alias  src"
          "  -----------------------"
          "  cli          cli    ---"
          "  core         core   ---"
          "  development  dev    -x-"]
         (str/split-lines
           (text-table-env/table environments changes "none")))))
