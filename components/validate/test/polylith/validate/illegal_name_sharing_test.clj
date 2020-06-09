(ns polylith.validate.illegal-name-sharing-test
  (:require [clojure.test :refer :all]
            [polylith.validate.illegal-name-sharing :as illegal-name-sharing]))

(def interfaces '[{:name "mybase"
                   :declarations [{:name add-two, :type function, :signature [x]}]
                   :implemented-by ["mybase"]}])

(def components '[{:name "mybase1"
                   :interface {:name "mybase1",
                               :declarations [{:name add-two, :type function, :signature [x]}]}}])

(def bases [{:name "mybase",}])

(def interface-names (set (map :name interfaces)))

(deftest errors--when-a-base-and-a-comonent-or-interface-have-the-same-name--return-errors
  (is (= ["A Base can't have the same name as an interface or component: mybase"]
         (illegal-name-sharing/errors interface-names components bases))))
