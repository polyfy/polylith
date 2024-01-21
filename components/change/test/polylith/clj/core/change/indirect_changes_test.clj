(ns polylith.clj.core.change.indirect-changes-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.indirect-changes :as indirect-changes]))

(def deps {:deps {"workspace-clj" {:src {:direct ["common" "file" "util"], :indirect []}}
                  "test-runner"   {:src {:direct ["common" "file" "util"], :indirect []}}
                  "command"       {:src {:direct ["common" "help" "test-runner" "util" "workspace"]
                                         :indirect ["deps" "file" "text-table" "validator"]}}
                  "text-table"    {:src {:direct ["util"], :indirect []}}
                  "util"          {:src {:direct [], :indirect []}}
                  "validator"     {:src {:direct ["common" "deps" "util"], :indirect []}}
                  "shell"         {:src {:direct [], :indirect []}}
                  "workspace"     {:src {:direct ["common" "deps" "file" "text-table" "util" "validator"], :indirect []}}
                  "cli"           {:src {:direct ["change" "command" "file" "workspace" "workspace-clj"]
                                         :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validator"]}}
                  "git"           {:src {:direct ["shell"], :indirect []}}
                  "deps"          {:src {:direct [], :indirect []}}
                  "help"          {:src {:direct ["util"], :indirect []}}
                  "file"          {:src {:direct [], :indirect []}}
                  "z-jocke"       {:src {:direct ["change" "util" "workspace" "workspace-clj"]
                                         :indirect ["common" "deps" "file" "git" "shell" "text-table" "validator"]}}
                  "common"        {:src {:direct ["util"], :indirect []}}
                  "change"        {:src {:direct ["git" "util"], :indirect ["shell"]}}}})

(deftest with-indirect-changes
  (is (= {:src  ["cli"
                 "command"
                 "z-jocke"]
          :test []}
         (:indirect-changes (indirect-changes/with-indirect-changes deps #{"change" "help"})))))