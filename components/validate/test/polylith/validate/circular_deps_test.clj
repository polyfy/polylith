(ns polylith.validate.circular-deps-test
  (:require [clojure.test :refer :all]
            [polylith.validate.circular-deps :as circular-deps]))

(deftest errors--interfaces-with-circular-dependencies--should-return-an-error
  (is (= ["Circular dependencies was found: invoice > user > payment > invoice"]
         (circular-deps/errors '[{:name "auth",
                                  :implementing-deps []}
                                 {:name "invoice",
                                  :implementing-deps ["user"]}
                                 {:name "payment",
                                  :implementing-deps ["invoice"]}
                                 {:name "user",
                                  :implementing-deps ["payment" "auth"]}]))))
