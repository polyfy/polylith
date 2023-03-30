(ns polylith.clj.core.user-input.params-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.user-input.params :as params]))

(deftest extract--named-and-unamed-arguments--returns-expected-result
  (is (= (params/extract ["w"
                          "name:x"
                          "param:"
                          "top-ns:se.example"
                          ":flag"
                          "abc"
                          "ws-dir:http://"
                          "project:cli:core"
                          "quoted1:\"value1\":value2"
                          "quoted2:\"value:1\":value2"
                          "quoted3:value1:\"value:2\":value3"
                          "quoted4:\"one two three\":value2"
                          nil] #{"ws-dir"})
         {:named-args   {:flag!   "true"
                         :name    "x"
                         :param   ""
                         :top-ns  "se.example"
                         :ws-dir  "http://"
                         :project ["cli" "core"]
                         :quoted1 ["\"value1\"" "value2"]
                         :quoted2 ["\"value:1\"" "value2"]
                         :quoted3 ["value1" "\"value:2\"" "value3"]
                         :quoted4 ["\"one two three\"" "value2"]}
          :unnamed-args ["w" "abc"]})))
