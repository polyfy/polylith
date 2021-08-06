(ns polylith.clj.core.change.indirect-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.indirect :as indirect]))

(def deps [["cli"
            {"workspace-clj" {:src {:direct ["common" "file" "util"], :indirect []}}
             "test-runner" {:src {:direct ["common" "file" "util"], :indirect []}}
             "command" {:src {:direct ["common" "help" "test-runner" "util" "workspace"]
                              :indirect ["deps" "file" "text-table" "validator"]}}
             "text-table" {:src {:direct ["util"], :indirect []}}
             "util" {:src {:direct [], :indirect []}}
             "validator" {:src {:direct ["common" "deps" "util"], :indirect []}}
             "shell" {:src {:direct [], :indirect []}}
             "workspace" {:src {:direct ["common" "deps" "file" "text-table" "util" "validator"], :indirect []}}
             "cli" {:src {:direct ["change" "command" "file" "workspace" "workspace-clj"]
                          :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validator"]}}
             "git" {:src {:direct ["shell"], :indirect []}}
             "deps" {:src {:direct [], :indirect []}}
             "help" {:src {:direct ["util"], :indirect []}}
             "file" {:src {:direct [], :indirect []}}
             "z-jocke" {:src {:direct ["change" "util" "workspace" "workspace-clj"]
                              :indirect ["common" "deps" "file" "git" "shell" "text-table" "validator"]}}
             "common" {:src {:direct ["util"], :indirect []}}
             "change" {:src {:direct ["git" "util"], :indirect ["shell"]}}}]
           ["core"
            {"workspace-clj" {:src {:direct ["common" "file" "util"], :indirect []}}
             "test-runner" {:src {:direct ["common" "file" "util"], :indirect []}}
             "command" {:src {:direct ["common" "help" "util" "workspace"], :indirect ["deps" "file" "text-table" "validator"]}}
             "text-table" {:src {:direct ["util"], :indirect []}}
             "util" {:src {:direct [], :indirect []}}
             "validator" {:src {:direct ["common" "deps" "util"], :indirect []}}
             "shell" {:src {:direct [], :indirect []}}
             "workspace" {:src {:direct ["common" "deps" "file" "text-table" "util" "validator"], :indirect []}}
             "cli" {:src {:direct ["change" "file" "workspace"]
                          :indirect ["common" "deps" "git" "shell" "text-table" "util" "validator"]}}
             "git" {:src {:direct ["shell"], :indirect []}}
             "deps" {:src {:direct [], :indirect []}}
             "help" {:src {:direct ["util"], :indirect []}}
             "file" {:src {:direct [], :indirect []}}
             "z-jocke" {:src {:direct ["change" "util" "workspace"]
                              :indirect ["common" "deps" "file" "git" "shell" "text-table" "validator"]}}
             "common" {:src {:direct ["util"], :indirect []}}
             "change" {:src {:direct ["git" "util"], :indirect ["shell"]}}}]
           ["dev"
            {"workspace-clj" {:src {:direct ["common" "file" "util"], :indirect []}}
             "test-runner" {:src {:direct ["common" "file" "util"], :indirect []}}
             "command" {:src {:direct ["common" "help" "test-runner" "util" "workspace"]
                              :indirect ["deps" "file" "text-table" "validator"]}}
             "text-table" {:src {:direct ["util"], :indirect []}}
             "util" {:src {:direct [], :indirect []}}
             "validator" {:src {:direct ["common" "deps" "util"], :indirect []}}
             "shell" {:src {:direct [], :indirect []}}
             "workspace" {:src {:direct ["common" "deps" "file" "text-table" "util" "validator"], :indirect []}}
             "cli" {:src {:direct ["change" "command" "file" "workspace" "workspace-clj"]
                          :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validator"]}}
             "git" {:src {:direct ["shell"], :indirect []}}
             "deps" {:src {:direct [], :indirect []}}
             "help" {:src {:direct ["util"], :indirect []}}
             "file" {:src {:direct [], :indirect []}}
             "z-jocke" {:src {:direct ["change" "util" "workspace" "workspace-clj"]
                              :indirect ["common" "deps" "file" "git" "shell" "text-table" "validator"]}}
             "common" {:src {:direct ["util"], :indirect []}}
             "change" {:src {:direct ["git" "util"], :indirect ["shell"]}}}]])

(deftest project-to-indirect-changes--based-on-dependencies-and-changed-components--return-indirectly-changed-bricks-per-project
  (is (= {"cli"  {:src  ["cli"
                         "command"
                         "z-jocke"]
                  :test []}
          "core" {:src  ["cli"
                         "command"
                         "z-jocke"]
                  :test []}
          "dev"  {:src  ["cli"
                         "command"
                         "z-jocke"]
                  :test []}}
         (indirect/project-to-indirect-changes deps #{"change" "help"}))))
