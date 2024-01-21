(ns polylith.clj.core.info.project-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.info.table.project :as project]))

(def workspace {:ws-dir "../poly-example/ws50"
                :name "ws50"
                :settings {:top-namespace "se.example"
                           :interface-ns "interface"
                           :thousand-separator ","
                           :color-mode "none"}
                :profiles []
                :projects [{:name "core"
                            :alias "core"
                            :type "project"
                            :is-dev false
                            :bricks-to-test []
                            :lines-of-code {:src 1, :test 1}
                            :paths {:src ["projects/core/resources"
                                          "projects/core/src"
                                          "projects/core/test"]}}
                           {:name "invoice"
                            :alias "inv"
                            :type "project"
                            :is-dev false
                            :bricks-to-test ["admin" "cli" "database" "invoicer" "purchaser"]
                            :lines-of-code {:src 0, :test 1}
                            :paths {:src ["bases/cli/resources"
                                          "bases/cli/src"
                                          "components/admin/resources"
                                          "components/admin/src"
                                          "components/database/resources"
                                          "components/database/src"
                                          "components/invoicer/resources"
                                          "components/invoicer/src"
                                          "components/purchaser/resources"
                                          "components/purchaser/src"]
                                    :test ["bases/cli/test"
                                           "components/admin/test"
                                           "components/database/test"
                                           "components/invoicer/test"
                                           "components/purchaser/test"
                                           "projects/invoice/test"]}}
                           {:name "development"
                            :alias "dev"
                            :type "project"
                            :is-dev true
                            :bricks-to-test []
                            :projects-to-test []
                            :lines-of-code {:src 4, :test 0}
                            :paths {:src ["bases/cli/resources"
                                          "bases/cli/src"
                                          "components/address/resources"
                                          "components/user/resources"
                                          "components/user/src"
                                          "development/src"
                                          "projects/core/src"]
                                    :test ["bases/cli/test"
                                           "components/address/test"
                                           "components/purchaser/test"
                                           "components/user/test"
                                           "projects/invoice/test"]}}]
                :changes {:sha1 "HEAD"
                          :git-diff-command "git diff HEAD --name-only"
                          :changed-components ["address" "admin" "database" "invoicer" "purchaser" "user"]
                          :changed-bases ["cli"]
                          :changed-projects ["core" "invoice"]}
                :paths {:missing []}})

(def workspace-with-profiles (assoc workspace :profiles [{:name "default"
                                                          :type "profile"
                                                          :paths ["components/file/src"
                                                                  "components/file/test"
                                                                  "projects/core/test"]}]))

(deftest table--no-resources-flat--returns-correct-table
  (is (= ["  project      alias  status   dev"
          "  --------------------------   ---"
          "  core *       core    s--     s--"
          "  invoice *    inv     -t-     -t-"
          "  development  dev     s--     s--"]
         (project/table workspace false false))))

(deftest table--with-resources-flag--returns-correct-table
  (is (= ["  project      alias  status   dev "
          "  --------------------------   ----"
          "  core *       core    sr--    s---"
          "  invoice *    inv     --t-    --t-"
          "  development  dev     s---    s---"]
         (project/table workspace false true))))

(deftest table--projects-with-loc--returns-table-with-lines-of-code
  (is (= ["  project      alias  status   dev   loc  (t)"
          "  --------------------------   ---   --------"
          "  core *       core    s--     s--     1    1"
          "  invoice *    inv     -t-     -t-     0    1"
          "  development  dev     s--     s--     4    0"
          "                                       5    2"]
         (project/table workspace true false))))

(deftest table--with-profile--returns-correct-table
  (is (= ["  project      alias  status   dev  default   "
          "  --------------------------   ------------   "
          "  core *       core    s--     s--    -t      "
          "  invoice *    inv     -t-     -t-    --      "
          "  development  dev     s--     s--    --      "]
         (project/table workspace-with-profiles false false))))

(deftest table--with-profile-and-loc--returns-correct-table
  (is (= ["  project      alias  status   dev  default   loc  (t)"
          "  --------------------------   ------------   --------"
          "  core *       core    s--     s--    -t        1    1"
          "  invoice *    inv     -t-     -t-    --        0    1"
          "  development  dev     s--     s--    --        4    0"
          "                                                5    2"]
         (project/table workspace-with-profiles true false))))
