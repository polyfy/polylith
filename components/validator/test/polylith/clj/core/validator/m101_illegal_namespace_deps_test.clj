(ns polylith.clj.core.validator.m101-illegal-namespace-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m101-illegal-namespace-deps :as m101]))

(deftest brick-errors--without-errors--returns-no-errors
  (let [component {:name "common"
                   :type "component"
                   :top-namespace "polylith"
                   :interface {:name "common"}
                   :namespaces {:src [{:name "common/readimportsfromdisk.clj"
                                       :imports ["clojure.string"
                                                 "polylith.file.interface"]}
                                      {:name "common/abc.clj"
                                       :imports ["clojure.core"
                                                 "polylith.user.interface"
                                                 "polylith.cmd.interface.v2.core"
                                                 "polylith.invoice.interface"]}]}}]
    (is (= []
           (m101/brick-errors "polylith." component #{"spec" "cmd" "file" "invoice" "user"} "interface" color/none)))))

(deftest brick-errors--with-errors--returns-errors
  (let [component {:name "common"
                   :type "component"
                   :top-namespace "polylith"
                   :interface {:name "common"}
                   :namespaces {:src '[{:name "purchase"
                                        :imports ["clojure.string"
                                                  "polylith.file.ifc"
                                                  "polylith.invoice.core"]}
                                       {:name "billing"
                                        :imports ["clojure.core"
                                                  "polylith.user.ifc"
                                                  "polylith.cmd.core"]}]}}]
    (is (= [{:type "error"
             :code 101
             :bricks ["common"]
             :colorized-message "Illegal dependency on namespace invoice.core in common.purchase. Use invoice.ifc instead to fix the problem."
             :message           "Illegal dependency on namespace invoice.core in common.purchase. Use invoice.ifc instead to fix the problem."}

            {:type "error"
             :code 101
             :bricks ["common"]
             :colorized-message "Illegal dependency on namespace cmd.core in common.billing. Use cmd.ifc instead to fix the problem."
             :message           "Illegal dependency on namespace cmd.core in common.billing. Use cmd.ifc instead to fix the problem."}]

           (m101/brick-errors "polylith." component #{"spec" "cmd" "file" "invoice" "user"} "ifc" color/none)))))

(deftest errors--component-with-mismatching-interface-name--returns-no-errors
  (let [component {:type "component"
                   :name "user1"
                   :top-namespace "polylith"
                   :interface {:name "user"}
                   :namespaces {:src [{:name "user/interface.clj"
                                       :imports ["user.core"]}
                                      {:name "user/core.clj"
                                       :imports []}]}}]
    (is (= []
           (m101/errors "polylith."  #{"user"} [component] [] "interface" color/none)))))
