(ns polylith.workspace.validate.interface-deps-test
  (:require [clojure.test :refer :all]
            [polylith.workspace.validate.interface-deps :as ideps]))

(deftest dependencies--without-errors--returns-no-errors
  (let [component '{:name "common"
                    :type "component"
                    :interface {:name "common"}
                    :imports [{:ns-path "/common:/readimportsfromdisk.clj"
                               :imports [clojure.string
                                         polylith.file.interface]}
                              {:ns-path "/common/abc.clj"
                               :imports [clojure.core
                                         polylith.user.interface
                                         polylith.cmd.interface.v2.core
                                         polylith.invoice.interface]}]}]
    (is (= []
           (ideps/errors "polylith." component #{"spec" "cmd" "file" "invoice" "user"} [])))))


(deftest dependencies--with-errors--returns-errors
  (let [component {:name "common"
                   :type "component"
                   :interface {:name "common"}
                   :imports '[{:ns-path "/common/purchase.clj"
                               :imports [clojure.string
                                         polylith.file.interface
                                         polylith.invoice.core]}
                              {:ns-path "/common/billing.clj"
                               :imports [clojure.core
                                         polylith.user.interface
                                         polylith.cmd.core]}]}]
    (is (= ["Illegal dependency on namespace 'invoice.core' in 'components//common/purchase.clj'. Import 'invoice.interface' instead to solve the problem."
            "Illegal dependency on namespace 'cmd.core' in 'components//common/billing.clj'. Import 'cmd.interface' instead to solve the problem."]
           (ideps/errors "polylith." component #{"spec" "cmd" "file" "invoice" "user"} [])))))

(deftest dependencies--component-with-mismatching-interface-name--returns-no-errors
  (let [component '{:type "component",
                    :name "user1",
                    :interface {:name "user"}
                    :imports [{:ns-path "user/interface.clj"
                               :imports [user.core]}
                              {:ns-path "user/core.clj"
                               :imports []}]}]
    (is (= []
           (ideps/errors "polylith." component #{"user"} [])))))
