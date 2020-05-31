(ns polylith.workspace.interface-deps-test
  (:require [clojure.test :refer :all]
            [polylith.workspace.interface-deps :as ideps]))

(deftest dependency--without-top-namespace--returns-dependencies
  (is (= {:ns-path "path"
          :depends-on-interface "file"
          :depends-on-ns "interface"}
         (ideps/dependency "" "common" "path" #{"spec" "cmd" "file" "common"}
                           "file.interface"))))

(deftest dependency--with-top-namespace--returns-dependencies
  (is (= {:ns-path "path"
          :depends-on-interface "file"
          :depends-on-ns "interface"}
         (ideps/dependency "polylith." "common" "path" #{"spec" "cmd" "file" "common"}
                           "polylith.file.interface"))))

(deftest brick-ns-dependencies--with-top-namespace--returns-dependencies
  (is (= [{:depends-on-interface "cmd"
           :depends-on-ns        "core"
           :ns-path              "/common/core.clj"}
          {:depends-on-interface "invoice"
           :depends-on-ns        "core"
           :ns-path              "/common/core.clj"}]
         (ideps/brick-ns-dependencies "polylith." "common" #{"spec" "cmd" "file" "invoice"}
                                      '{:ns-path "/common/core.clj"
                                        :imports [clojure.core
                                                  polylith.user.interface
                                                  polylith.cmd.core
                                                  polylith.invoice.core]}))))

(deftest brick-dependencies--with-top-namespace--returns-dependencies
  (is (= [{:depends-on-interface "user"
           :depends-on-ns        "interface"
           :ns-path              "/common/abc.clj"}
          {:depends-on-interface "cmd"
           :depends-on-ns        "core"
           :ns-path              "/common/abc.clj"}
          {:depends-on-interface "invoice"
           :depends-on-ns        "core"
                    :ns-path     "/common/abc.clj"}]
         (ideps/brick-dependencies "polylith." "common" #{"spec" "cmd" "user" "invoice"}
                                   '[{:ns-path "/common/core.clj"
                                      :imports [clojure.string
                                                polylith.file.interface]}
                                     {:ns-path "/common/abc.clj"
                                      :imports [clojure.core
                                                polylith.user.interface
                                                polylith.cmd.core
                                                polylith.invoice.core]}]))))

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
