(ns polylith.clj.core.workspace.text-table.env-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.text-table.new-env-table :as env-table]))

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
                                :active? true
                                :dev? false
                                :lines-of-code-src 1
                                :lines-of-code-test 1
                                :src-paths ["environments/core/resources"
                                            "environments/core/src"
                                            "environments/core/test"]
                                :test-paths []}
                               {:name "development"
                                :alias "dev"
                                :type "environment"
                                :active? false
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
                                             "components/user/test"]}
                               {:name "invoice"
                                :alias "inv"
                                :type "environment"
                                :active? true
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
                                             "environments/invoice/test"]}]
                :changes {:sha1 "HEAD"
                          :git-command "git diff HEAD --name-only"
                          :user-input {:run-all? false
                                       :run-env-tests? false
                                       :interface nil
                                       :unnamed-args []
                                       :active-dev-profiles #{:default}
                                       :selected-environments #{}}
                          :changed-components ["address" "admin" "database" "invoicer" "purchaser" "user"]
                          :changed-bases ["cli"]
                          :changed-environments ["core" "invoice"]
                          :env->indirect-changes {"core" [], "development" [], "invoice" []}
                          :env->bricks-to-test {"core" []
                                                "development" []
                                                "invoice" ["admin" "cli" "database" "invoicer" "purchaser"]}
                          :environments-to-test []}})

(deftest table--environments-with-no-direct-change-but-with-tests-to-run--returns-correct-table
  (is (= ["  environment  alias  src "
          "  ------------------------"
          "  core *       core   xx--"
          "  invoice *    inv    --x-"]
         (env-table/table workspace false))))

(deftest table--environments-with-loc--returns-table-with-lines-of-code
  (is (= ["  environment  alias  src   loc  (t)"
          "  ----------------------------------"
          "  core *       core   xx--    1    1"
          "  invoice *    inv    --x-    0    1"
          "                              1    2"]
         (env-table/table workspace true))))
