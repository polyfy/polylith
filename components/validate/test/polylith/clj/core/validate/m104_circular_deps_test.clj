(ns polylith.clj.core.validate.m104-circular-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.validate.m104-circular-deps :as m104]))

(def environments [{:name "cli",
                    :brick-deps {"workspace-clj" {:direct ["common" "file" "util"], :indirect []},
                                 "test-runner" {:direct ["common" "file" "util"], :indirect []},
                                 "command" {:circular ["command" "help" "command"],
                                            :direct ["common" "help" "test-runner" "util" "workspace"],
                                            :indirect ["command" "deps" "file" "text-table" "validate"]},
                                 "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validate"], :indirect []},
                                 "cli" {:direct ["change" "command" "file" "workspace" "workspace-clj"],
                                        :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validate"]},
                                 "help" {:circular ["help" "command" "help"],
                                         :direct ["command" "util"],
                                         :indirect ["common" "deps" "file" "help" "test-runner" "text-table" "validate" "workspace"]},}}
                   {:name "core",
                    :brick-deps {"workspace-clj" {:direct ["common" "file" "util"], :indirect []},
                                 "test-runner" {:direct ["common" "file" "util"], :indirect []},
                                 "command" {:direct ["common" "help" "util" "workspace"],
                                            :indirect ["deps" "file" "text-table" "validate"]},
                                 "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validate"], :indirect []},
                                 "cli" {:direct ["change" "file" "workspace"],
                                        :indirect ["common" "deps" "git" "shell" "text-table" "util" "validate"]},
                                 "help" {:direct ["util"], :indirect []},}}
                   {:name "dev",
                    :brick-deps {"workspace-clj" {:direct ["common" "file" "util"], :indirect []},
                                 "test-runner" {:direct ["common" "file" "util"], :indirect []},
                                 "command" {:circular ["command" "help" "command"],
                                            :direct ["common" "help" "test-runner" "util" "workspace"],
                                            :indirect ["command" "deps" "file" "text-table" "validate"]},
                                 "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validate"], :indirect []},
                                 "cli" {:direct ["change" "command" "file" "workspace" "workspace-clj"],
                                        :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validate"]},
                                 "help" {:circular ["help" "command" "help"],
                                         :direct ["command" "util"],
                                         :indirect ["common" "deps" "file" "help" "test-runner" "text-table" "validate" "workspace"]},}}])

(deftest errors--an-environment-with-circular-dependencies--should-return-an-error
  (is (= [{:type "error"
           :code 104
           :environment "cli"
           :message "A circular dependency was found in the cli environment: command > help > command"
           :colorized-message "A circular dependency was found in the cli environment: command > help > command"
           :components ["command" "help"]}]
         (m104/errors environments color/none))))

(deftest errors--when-having-no-environments--return-no-errors
  (is (= []
         (m104/errors [] color/none))))
