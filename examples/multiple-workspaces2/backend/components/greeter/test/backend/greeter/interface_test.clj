(ns backend.greeter.interface-test
  (:require [clojure.test :refer :all]
            [backend.greeter.interface :as greeter]))

(deftest hello-test
  (let [message (with-out-str
                  (greeter/hello "James"))]
    (is (= message "Hello James!\n"))))
