(ns polylith.clj.core.command.cmd-validator.cant-execute-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.command.cmd-validator.executable :as executable]))

(deftest when-executed-outside-the-workspace
  (is (= (executable/cant-execute-test? nil "test" nil false)
         true)))

(deftest when-test-command-and-none-of-ws-dir-or-ws-file-is-set
  (is (= (executable/cant-execute-test? {} "test" nil false)
         false)))

(deftest when-test-command-and-ws-dir-is-set
  (is (= (executable/cant-execute-test? {} "test" ".." false)
         true)))

(deftest when-create-command-and-find-parent-dir-is-set
  (is (= (executable/cant-execute-test? {} "test" nil true)
         true)))
