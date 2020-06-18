(ns polylith.validate.illegal-namespace-deps-test
  (:require [clojure.test :refer :all]
            [polylith.validate.illegal-namespace-deps :as ideps]))

(deftest brick-errors--without-errors--returns-no-errors
  (let [component '{:name "common"
                    :type "component"
                    :interface {:name "common"}
                    :src-namespaces [{:name "common/readimportsfromdisk.clj"
                                      :imports ["clojure.string"
                                                "polylith.file.interface"]}
                                     {:name "common/abc.clj"
                                      :imports ["clojure.core"
                                                "polylith.user.interface"
                                                "polylith.cmd.interface.v2.core"
                                                "polylith.invoice.interface"]}]}]
    (is (= []
           (ideps/brick-errors "polylith." component #{"spec" "cmd" "file" "invoice" "user"} [])))))

(deftest brick-errors--with-errors--returns-errors
  (let [component {:name "common"
                   :type "component"
                   :interface {:name "common"}
                   :src-namespaces '[{:name "purchase"
                                      :imports ["clojure.string"
                                                "polylith.file.interface"
                                                "polylith.invoice.core"]}
                                     {:name "billing"
                                      :imports ["clojure.core"
                                                "polylith.user.interface"
                                                "polylith.cmd.core"]}]}]
    (is (= ["Illegal dependency on namespace 'invoice.core' in namespace 'purchase' in the 'common' component. Use 'invoice.interface' instead to fix the problem."
            "Illegal dependency on namespace 'cmd.core' in namespace 'billing' in the 'common' component. Use 'cmd.interface' instead to fix the problem."]
           (ideps/brick-errors "polylith." component #{"spec" "cmd" "file" "invoice" "user"} [])))))

(deftest errors--component-with-mismatching-interface-name--returns-no-errors
  (let [component '{:type "component",
                    :name "user1",
                    :interface {:name "user"}
                    :src-namespaces [{:name "user/interface.clj"
                                      :imports ["user.core"]}
                                     {:name "user/core.clj"
                                      :imports []}]}]
    (is (= []
           (ideps/errors "polylith." component #{"user"} [])))))
