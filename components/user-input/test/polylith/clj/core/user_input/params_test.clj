(ns polylith.clj.core.user-input.params-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.user-input.params :as params]))

(deftest extract--named-and-unamed-arguments--returns-expected-result
  (is (= {:named-args   {:project ["cli" "core"]
                         :flag! "true"
                         :name "x"
                         :param ""
                         :top-ns "se.example"
                         :ws-dir "http://"}
          :unnamed-args ["w"
                         "abc"]}
         (params/extract ["w"
                          "name:x"
                          "param:"
                          "top-ns:se.example"
                          "project:cli:core"
                          ":flag"
                          "abc"
                          "ws-dir:http://"
                          nil] #{"ws-dir"}))))
