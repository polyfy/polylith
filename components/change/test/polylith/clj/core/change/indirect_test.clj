(ns polylith.clj.core.change.indirect-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.indirect :as indirect]))

(def deps [["cli"
            {"workspace-clj" {:direct ["common" "file" "util"], :indirect []}
             "test-runner" {:direct ["common" "file" "util"], :indirect []}
             "command" {:direct ["common" "help" "test-runner" "util" "workspace"]
                        :indirect ["deps" "file" "text-table" "validate"]}
             "text-table" {:direct ["util"], :indirect []}
             "util" {:direct [], :indirect []}
             "validate" {:direct ["common" "deps" "util"], :indirect []}
             "shell" {:direct [], :indirect []}
             "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validate"], :indirect []}
             "cli" {:direct ["change" "command" "file" "workspace" "workspace-clj"]
                    :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validate"]}
             "git" {:direct ["shell"], :indirect []}
             "deps" {:direct [], :indirect []}
             "help" {:direct ["util"], :indirect []}
             "file" {:direct [], :indirect []}
             "z-jocke" {:direct ["change" "util" "workspace" "workspace-clj"]
                        :indirect ["common" "deps" "file" "git" "shell" "text-table" "validate"]}
             "common" {:direct ["util"], :indirect []}
             "change" {:direct ["git" "util"], :indirect ["shell"]}}]
           ["core"
            {"workspace-clj" {:direct ["common" "file" "util"], :indirect []}
             "test-runner" {:direct ["common" "file" "util"], :indirect []}
             "command" {:direct ["common" "help" "util" "workspace"], :indirect ["deps" "file" "text-table" "validate"]}
             "text-table" {:direct ["util"], :indirect []}
             "util" {:direct [], :indirect []}
             "validate" {:direct ["common" "deps" "util"], :indirect []}
             "shell" {:direct [], :indirect []}
             "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validate"], :indirect []}
             "cli" {:direct ["change" "file" "workspace"]
                    :indirect ["common" "deps" "git" "shell" "text-table" "util" "validate"]}
             "git" {:direct ["shell"], :indirect []}
             "deps" {:direct [], :indirect []}
             "help" {:direct ["util"], :indirect []}
             "file" {:direct [], :indirect []}
             "z-jocke" {:direct ["change" "util" "workspace"]
                        :indirect ["common" "deps" "file" "git" "shell" "text-table" "validate"]}
             "common" {:direct ["util"], :indirect []}
             "change" {:direct ["git" "util"], :indirect ["shell"]}}]
           ["dev"
            {"workspace-clj" {:direct ["common" "file" "util"], :indirect []}
             "test-runner" {:direct ["common" "file" "util"], :indirect []}
             "command" {:direct ["common" "help" "test-runner" "util" "workspace"]
                        :indirect ["deps" "file" "text-table" "validate"]}
             "text-table" {:direct ["util"], :indirect []}
             "util" {:direct [], :indirect []}
             "validate" {:direct ["common" "deps" "util"], :indirect []}
             "shell" {:direct [], :indirect []}
             "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validate"], :indirect []}
             "cli" {:direct ["change" "command" "file" "workspace" "workspace-clj"]
                    :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validate"]}
             "git" {:direct ["shell"], :indirect []}
             "deps" {:direct [], :indirect []}
             "help" {:direct ["util"], :indirect []}
             "file" {:direct [], :indirect []}
             "z-jocke" {:direct ["change" "util" "workspace" "workspace-clj"]
                        :indirect ["common" "deps" "file" "git" "shell" "text-table" "validate"]}
             "common" {:direct ["util"], :indirect []}
             "change" {:direct ["git" "util"], :indirect ["shell"]}}]])

(deftest env->indirect-changes--based-on-dependencies-and-changed-components--return-indirectly-changed-bricks-per-environment
  (is (= {"cli" ["cli" "command" "z-jocke"]
          "core" ["cli" "command" "z-jocke"]
          "dev" ["cli" "command" "z-jocke"]}
         (indirect/env->indirect-changes deps #{"change" "help"}))))
