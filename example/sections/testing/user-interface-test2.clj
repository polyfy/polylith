(ns se.example.user.interface-test
  (:require [clojure.test :refer :all]
            [se.example.user.interface :as user]))

(deftest hello--when-called-with-a-name--then-return-hello-phrase
         (is (= "Hello Lisa!!"
                (user-core/hello "Lisa"))))
