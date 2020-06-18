(ns polylith.validate.illegal-namespace-deps-test
  (:require [clojure.test :refer :all]
            [polylith.validate.illegal-namespace-deps :as ideps]))

(deftest brick-errors--without-errors--returns-no-errors
  (let [component '{:name "common"
                    :type "component"
                    :interface {:name "common"}
                    :namespaces [{:ns-path "common:/readimportsfromdisk.clj"
                                  :imports ["clojure.string"
                                            "polylith.file.interface"]}
                                 {:ns-path "common/abc.clj"
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
                   :namespaces '[{:ns-path "common/purchase.clj"
                                  :imports ["clojure.string"
                                            "polylith.file.interface"
                                            "polylith.invoice.core"]}
                                 {:ns-path "common/billing.clj"
                                  :imports ["clojure.core"
                                            "polylith.user.interface"
                                            "polylith.cmd.core"]}]}]
    (is (= ["Illegal dependency on namespace 'invoice.core' in 'components/common/purchase.clj'. Use 'invoice.interface' instead to fix the problem."
            "Illegal dependency on namespace 'cmd.core' in 'components/common/billing.clj'. Use 'cmd.interface' instead to fix the problem."]
           (ideps/brick-errors "polylith." component #{"spec" "cmd" "file" "invoice" "user"} [])))))

(deftest errors--component-with-mismatching-interface-name--returns-no-errors
  (let [component '{:type "component",
                    :name "user1",
                    :interface {:name "user"}
                    :namespaces [{:ns-path "user/interface.clj"
                                  :imports ["user.core"]}
                                 {:ns-path "user/core.clj"
                                  :imports []}]}]
    (is (= []
           (ideps/errors "polylith." component #{"user"} [])))))
