(ns polylith.clj.core.create.component-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interfc :as helper]))

(deftest create-component--
  (let [ws-name "ws1"
        output (with-out-str
                 (helper/execute-command "" "create" "w" ws-name "se.example")
                 (helper/execute-command ws-name "create" "c" "my-component"))]
    (is (= ""
           output))))
