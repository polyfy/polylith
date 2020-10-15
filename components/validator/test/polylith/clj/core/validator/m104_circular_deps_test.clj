(ns polylith.clj.core.validator.m104-circular-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m104-circular-deps :as m104]))

(def projects [{:name     "cli",
                :deps {"workspace-clj" {:direct ["common" "file" "util"], :indirect []},
                       "test-runner" {:direct ["common" "file" "util"], :indirect []},
                       "command" {:circular ["command" "help" "command"],
                                  :direct ["common" "help" "test-runner" "util" "workspace"],
                                  :indirect ["command" "deps" "file" "text-table" "validator"]},
                       "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validator"], :indirect []},
                       "cli" {:direct ["change" "command" "file" "workspace" "workspace-clj"],
                              :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validator"]},
                       "help" {:circular ["help" "command" "help"],
                               :direct ["command" "util"],
                               :indirect ["common" "deps" "file" "help" "test-runner" "text-table" "validator" "workspace"]},}}
               {:name "core",
                :deps {"workspace-clj" {:direct ["common" "file" "util"], :indirect []},
                       "test-runner" {:direct ["common" "file" "util"], :indirect []},
                       "command" {:direct ["common" "help" "util" "workspace"],
                                  :indirect ["deps" "file" "text-table" "validator"]},
                       "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validator"], :indirect []},
                       "cli" {:direct ["change" "file" "workspace"],
                              :indirect ["common" "deps" "git" "shell" "text-table" "util" "validator"]},
                       "help" {:direct ["util"], :indirect []},}}
               {:name "dev",
                :deps {"workspace-clj" {:direct ["common" "file" "util"], :indirect []},
                       "test-runner" {:direct ["common" "file" "util"], :indirect []},
                       "command" {:circular ["command" "help" "command"],
                                  :direct ["common" "help" "test-runner" "util" "workspace"],
                                  :indirect ["command" "deps" "file" "text-table" "validator"]},
                       "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validator"], :indirect []},
                       "cli" {:direct ["change" "command" "file" "workspace" "workspace-clj"],
                              :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validator"]},
                       "help" {:circular ["help" "command" "help"],
                               :direct ["command" "util"],
                               :indirect ["common" "deps" "file" "help" "test-runner" "text-table" "validator" "workspace"]},}}])

(deftest errors--an-project-with-circular-dependencies--should-return-an-error
  (is (= [{:type "error"
           :code 104
           :project "cli"
           :message "A circular dependency was found in the cli project: command > help > command"
           :colorized-message "A circular dependency was found in the cli project: command > help > command"
           :components ["command" "help"]}]
         (m104/errors projects color/none))))

(deftest errors--when-having-no-projects--return-no-errors
  (is (= []
         (m104/errors [] color/none))))
