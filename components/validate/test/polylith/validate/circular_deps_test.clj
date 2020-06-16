(ns polylith.validate.circular-deps-test
  (:require [clojure.test :refer :all]
            [polylith.validate.circular-deps :as circular-deps]))

(deftest errors--interfaces-with-circular-dependencies--should-return-an-error
  (is (= ["Circular dependencies was found: invoice > user > payment > invoice"]
         (circular-deps/errors '[{:name "auth",
                                  :interface-deps []}
                                 {:name "invoice",
                                  :interface-deps ["user"]}
                                 {:name "payment",
                                  :interface-deps ["invoice"]}
                                 {:name "user",
                                  :interface-deps ["payment" "auth"]}]))))
