(ns polylith.clj.core.workspace.text-table.env-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.workspace.text-table.env-table :as env-table]))

(def workspace {:ws-dir "../poly-example/ws50"
                :name "ws50"
                :settings {:top-namespace "se.example"
                           :profile->settings {}
                           :interface-ns "interface"
                           :thousand-sep ","
                           :color-mode "none"}
                :environments [{:name "core"
                                :alias "core"
                                :type "environment"
                                :run-tests? true
                                :dev? false
                                :lines-of-code-src 1
                                :lines-of-code-test 1
                                :src-paths ["environments/core/resources"
                                            "environments/core/src"
                                            "environments/core/test"]
                                :test-paths []}
                               {:name "invoice"
                                :alias "inv"
                                :type "environment"
                                :run-tests? true
                                :dev? false
                                :lines-of-code-src 0
                                :lines-of-code-test 1
                                :src-paths ["bases/cli/resources"
                                            "bases/cli/src"
                                            "components/admin/resources"
                                            "components/admin/src"
                                            "components/database/resources"
                                            "components/database/src"
                                            "components/invoicer/resources"
                                            "components/invoicer/src"
                                            "components/purchaser/resources"
                                            "components/purchaser/src"]
                                :test-paths ["bases/cli/test"
                                             "components/admin/test"
                                             "components/database/test"
                                             "components/invoicer/test"
                                             "components/purchaser/test"
                                             "environments/invoice/test"]}
                               {:name "development"
                                :alias "dev"
                                :type "environment"
                                :run-tests? false
                                :dev? true
                                :lines-of-code-src 4
                                :lines-of-code-test 0
                                :src-paths ["bases/cli/resources"
                                            "bases/cli/src"
                                            "components/address/resources"
                                            "components/user/resources"
                                            "components/user/src"
                                            "development/src"]
                                :test-paths ["bases/cli/test"
                                             "components/address/test"
                                             "components/purchaser/test"
                                             "components/user/test"]}]
                :changes {:sha1 "HEAD"
                          :git-command "git diff HEAD --name-only"
                          :changed-components ["address" "admin" "database" "invoicer" "purchaser" "user"]
                          :changed-bases ["cli"]
                          :changed-environments ["core" "invoice"]
                          :env->indirect-changes {"core" [], "development" [], "invoice" []}
                          :env->bricks-to-test {"core" []
                                                "development" []
                                                "invoice" ["admin" "cli" "database" "invoicer" "purchaser"]}
                          :environments-to-test []}
                :paths {:missing []}})

(def workspace-with-profiles (-> workspace
                                 (assoc-in [:settings :profile->settings] {"default" {:paths ["components/file/src"
                                                                                              "components/file/test"
                                                                                              "environments/core/test"]}})))

(deftest table--no-resources-flat--returns-correct-table
  (is (= ["  environment  alias  source"
          "  --------------------------"
          "  core *       core    x--  "
          "  invoice *    inv     -x-  "
          "  development  dev     x--  "]
         (env-table/table workspace false false))))

(deftest table--with-resources-flag--returns-correct-table
  (is (= ["  environment  alias  source"
          "  --------------------------"
          "  core *       core    xx-- "
          "  invoice *    inv     --x- "
          "  development  dev     x--- "]
         (env-table/table workspace false true))))

(deftest table--environments-with-loc--returns-table-with-lines-of-code
  (is (= ["  environment  alias  source   loc  (t)"
          "  --------------------------   --------"
          "  core *       core    x--       1    1"
          "  invoice *    inv     -x-       0    1"
          "  development  dev     x--       4    0"
          "                                 5    2"]
         (env-table/table workspace true false))))

(deftest table--with-profile--returns-correct-table
  (is (= ["  environment  alias  source   default   "
          "  --------------------------   -------   "
          "  core *       core    x--       -x      "
          "  invoice *    inv     -x-       --      "
          "  development  dev     x--       --      "]
         (env-table/table workspace-with-profiles false false))))

(deftest table--with-profile-and-loc--returns-correct-table
  (is (= ["  environment  alias  source   default   loc  (t)"
          "  --------------------------   -------   --------"
          "  core *       core    x--       -x        1    1"
          "  invoice *    inv     -x-       --        0    1"
          "  development  dev     x--       --        4    0"
          "                                           5    2"]
         (env-table/table workspace-with-profiles true false))))
