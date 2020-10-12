(ns polylith.clj.core.command.cmd-validator.executable-outside-ws-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.command.cmd-validator.executable :as executable]))

(deftest when-no-workspace-or-command-is-given
  (is (= true
         (executable/can-be-executed-outside-ws? nil nil nil))))

(deftest when-workspace-is-given
  (is (= false
         (executable/can-be-executed-outside-ws? {} nil nil))))

(deftest when-no-workspace-and-info-command-is-given
  (is (= false
         (executable/can-be-executed-outside-ws? nil "info" nil))))

(deftest when-no-workspace-and-help-command-is-given
  (is (= true
         (executable/can-be-executed-outside-ws? nil "help" nil))))

(deftest when-no-workspace-and-create-c-command-is-given
  (is (= false
         (executable/can-be-executed-outside-ws? nil "create" "c"))))

(deftest when-no-workspace-and-create-w-command-is-given
  (is (= true
         (executable/can-be-executed-outside-ws? nil "create" "w"))))

(deftest when-no-workspace-and-create-workspace-command-is-given
  (is (= true
         (executable/can-be-executed-outside-ws? nil "create" "workspace"))))
