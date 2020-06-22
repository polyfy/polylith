(ns polylith.validate.illegal-name-sharing-test
  (:require [clojure.test :refer :all]
            [polylith.validate.illegal-name-sharing :as illegal-name-sharing]))

(deftest errors--when-a-base-and-an-interface-but-not-a-component-shares-the-same-name--returns-error
  (let [interfaces '[{:name "mybase"}]
        components '[{:name "mybase1"
                      :interface {:name "mybase"}}]
        bases [{:name "mybase"}]
        interface-names (set (map :name interfaces))]
    (is (= [{:type "error"
             :code 105
             :message "A base can't have the same name as an interface or component: mybase"
             :interfaces ["mybase"]
             :components []
             :bases ["mybase"]}]
           (illegal-name-sharing/errors interface-names components bases)))))

(deftest errors--when-a-base-and-a-component-the-same-name--returns-error
  (let [interfaces '[{:name "mybase"}]
        components '[{:name "mybase"
                      :interface {:name "mybase"}}]
        bases [{:name "mybase"}]
        interface-names (set (map :name interfaces))]
    (is (= [{:type "error"
             :code 105
             :message "A base can't have the same name as an interface or component: mybase"
             :interfaces ["mybase"]
             :components ["mybase"]
             :bases ["mybase"]}]
           (illegal-name-sharing/errors interface-names components bases)))))
