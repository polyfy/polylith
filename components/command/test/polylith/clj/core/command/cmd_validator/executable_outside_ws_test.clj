(ns polylith.clj.core.command.cmd-validator.executable-outside-ws-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.command.cmd-validator.executable :as executable]))

(deftest when-no-workspace-nor-command-is-given
  (is (= (executable/cant-be-executed-outside-ws? nil nil nil)
         false)))

(deftest when-workspace-is-given
  (is (= (executable/cant-be-executed-outside-ws? {} nil nil)
         false)))

(deftest when-no-workspace-but-info-command-is-given
  (is (= (executable/cant-be-executed-outside-ws? nil "info" nil)
         true)))

(deftest when-no-workspace-but-help-command-is-given
  (is (= (executable/cant-be-executed-outside-ws? nil "help" nil)
         false)))

(deftest when-no-workspace-but-help-command-is-given
  (is (= (executable/cant-be-executed-outside-ws? nil "version" nil)
         false)))

(deftest when-no-workspace-but-create-c-command-is-given
  (is (= (executable/cant-be-executed-outside-ws? nil "create" [nil "c"])
         true)))

(deftest when-no-workspace-but-create-w-command-is-given
  (is (= (executable/cant-be-executed-outside-ws? nil "create" [nil "w"])
         false)))

(deftest when-no-workspace-but-create-workspace-command-is-given
  (is (= (executable/cant-be-executed-outside-ws? nil "create" [nil "workspace"])
         false)))
