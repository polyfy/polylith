(ns polylith.validate.m101-illegal-namespace-deps-test
  (:require [clojure.test :refer :all]
            [polylith.validate.m101-illegal-namespace-deps :as m101]))

(deftest brick-errors--without-errors--returns-no-errors
  (let [component '{:name "common"
                    :type "component"
                    :interface {:name "common"}
                    :namespaces-src [{:name "common/readimportsfromdisk.clj"
                                      :imports ["clojure.string"
                                                "polylith.file.interface"]}
                                     {:name "common/abc.clj"
                                      :imports ["clojure.core"
                                                "polylith.user.interface"
                                                "polylith.cmd.interface.v2.core"
                                                "polylith.invoice.interface"]}]}]
    (is (= []
           (m101/brick-errors "polylith." component #{"spec" "cmd" "file" "invoice" "user"} [])))))

(deftest brick-errors--with-errors--returns-errors
  (let [component {:name "common"
                   :type "component"
                   :interface {:name "common"}
                   :namespaces-src '[{:name "purchase"
                                      :imports ["clojure.string"
                                                "polylith.file.interface"
                                                "polylith.invoice.core"]}
                                     {:name "billing"
                                      :imports ["clojure.core"
                                                "polylith.user.interface"
                                                "polylith.cmd.core"]}]}]
    (is (= [{:type "error"
             :code 101
             :message "Illegal dependency on namespace invoice.core in common.purchase. Use invoice.interface instead to fix the problem."
             :bricks ["common"]}
            {:type "error"
             :code 101
             :message "Illegal dependency on namespace cmd.core in common.billing. Use cmd.interface instead to fix the problem."
             :bricks ["common"]}]
           (mapv #(select-keys % [:type :code :message :bricks])
                 (m101/brick-errors "polylith." component #{"spec" "cmd" "file" "invoice" "user"} []))))))

(deftest errors--component-with-mismatching-interface-name--returns-no-errors
  (let [component '{:type "component",
                    :name "user1",
                    :interface {:name "user"}
                    :namespaces-src [{:name "user/interface.clj"
                                      :imports ["user.core"]}
                                     {:name "user/core.clj"
                                      :imports []}]}]
    (is (= []
           (m101/errors "polylith." component #{"user"} [])))))
