(ns polylith.clj.core.command.cmd-validator.cant-execute-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.command.cmd-validator.executable :as executable]))

(deftest when-executed-outside-the-workspace
  (is (= true
         (executable/cant-execute-test? nil "test" nil false))))


(deftest when-test-command-and-none-of-ws-dir-or-ws-file-is-set
  (is (= false
         (executable/cant-execute-test? {} "test" nil false))))

(deftest when-test-command-and-ws-dir-is-set
  (is (= true
         (executable/cant-execute-test? {} "test" ".." false))))

(deftest when-create-command-and-find-parent-dir-is-set
  (is (= true
         (executable/cant-execute-test? {} "test" nil true))))
