(ns polylith.clj.core.command.cmd-validator.executable-ws-file-is-set-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.command.cmd-validator.executable :as executable]))

(deftest cant-execute-when-ws-file-is-set?--when-not-create-or-test-command--return-false
  (is (= (executable/cant-execute-when-ws-file-is-set? "info" nil)
         false)))

(deftest cant-execute-when-ws-file-is-set?--when-create-command-and-ws-file-not-given--return-false
  (is (= (executable/cant-execute-when-ws-file-is-set? "info" nil)
         false)))

(deftest cant-execute-when-ws-file-is-set?--when-test-command-and-ws-file-not-given--return-false
  (is (= (executable/cant-execute-when-ws-file-is-set? "test" nil)
         false)))

(deftest cant-execute-when-ws-file-is-set?--when-create-command-and-ws-file-is-given--return-true
  (is (= (executable/cant-execute-when-ws-file-is-set? "create" "ws.edn")
         true)))

(deftest cant-execute-when-ws-file-is-set?--when-test-command-and-ws-file-is-given--return-true
  (is (= (executable/cant-execute-when-ws-file-is-set? "test" "ws.edn")
         true)))
