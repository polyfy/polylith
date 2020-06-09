(ns polylith.validate.missing-data-test
  (:require [clojure.test :refer :all]
            [polylith.validate.missing-data :as missing-data]))

(def interfaces '[{:name "invoice"
                   :type "interface"
                   :declarations [{:type data, :name abc}
                                  {:name func1, :type function, :signature [a]}]
                   :implemented-by ["invoice" "invoice2"]}
                  {:name "user"
                   :type "interface",
                   :declarations [{:type data, :name data1}
                                  {:name func1, :type function, :signature [a]}]
                   :implemented-by ["user1" "user2"]}])

(def components '[{:name "invoice"
                   :type "component"
                   :interface {:name "invoice"
                               :declarations [{:type data, :name abc}
                                              {:name func1, :type function, :signature [a]}]}}
                  {:name "invoice2"
                   :type "component"
                   :interface {:name "invoice"
                               :declarations [{:type data, :name abc}
                                              {:name func1, :type function, :signature [a]}]}}
                  {:name "user1"
                   :type "component"
                   :interface {:name "user"
                               :declarations [{:type data, :name data1}
                                              {:name func1, :type function, :signature []}]}}
                  {:name "user2"
                   :type "component"
                   :interface {:name "user"
                               :declarations [{:name func2, :type function, :signature [x y]}]}}])

(deftest errors--missing-data-in-a-component--returns-error
  (is (= ["Missing data definition in the user2 component: data1"]
         (missing-data/errors interfaces components))))
