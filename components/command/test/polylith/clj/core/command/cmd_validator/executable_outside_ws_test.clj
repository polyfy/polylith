(ns polylith.clj.core.command.cmd-validator.executable-outside-ws-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.command.cmd-validator.executable :as executable]))

(deftest when-no-workspace-nor-command-is-given
  (is (= false
         (executable/cant-be-executed-outside-ws? nil nil nil))))

(deftest when-workspace-is-given
  (is (= false
         (executable/cant-be-executed-outside-ws? {} nil nil))))

(deftest when-no-workspace-but-info-command-is-given
  (is (= true
         (executable/cant-be-executed-outside-ws? nil "info" nil))))

(deftest when-no-workspace-but-help-command-is-given
  (is (= false
         (executable/cant-be-executed-outside-ws? nil "help" nil))))

(deftest when-no-workspace-but-help-command-is-given
  (is (= false
         (executable/cant-be-executed-outside-ws? nil "version" nil))))

(deftest when-no-workspace-but-create-c-command-is-given
  (is (= true
         (executable/cant-be-executed-outside-ws? nil "create" [nil "c"]))))

(deftest when-no-workspace-but-create-w-command-is-given
  (is (= false
         (executable/cant-be-executed-outside-ws? nil "create" [nil "w"]))))

(deftest when-no-workspace-but-create-workspace-command-is-given
  (is (= false
         (executable/cant-be-executed-outside-ws? nil "create" [nil "workspace"]))))
