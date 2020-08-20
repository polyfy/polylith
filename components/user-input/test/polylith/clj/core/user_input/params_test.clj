(ns polylith.clj.core.user-input.params-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.user-input.params :as params]))

(deftest extract--named-and-unamed-arguments--returns-expected-result
  (is (= {:unnamed-args ["w" "abc"]
          :named-args {:flag! "true"
                       :env ["cli" "core"]
                       :name "x"
                       :top-ns "se.example"}}
         (params/extract ["w"
                          "name:x"
                          "top-ns:se.example"
                          "env:cli:core"
                          ":flag"
                          "abc"
                          nil]))))
