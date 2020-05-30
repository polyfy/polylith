(ns polylith.workspace.test-dependencies
  (:require [clojure.test :refer :all]
            [polylith.workspace.dependencies :as core]))

(deftest dependency--without-top-namespace--returns-dependencies
  (is (= {:ns-path "path"
          :depends-on-interface "file"
          :depends-on-ns "interface"}
         (core/dependency "" "common" "path" #{"spec" "cmd" "file" "common"}
                          "file.interface"))))

(deftest dependency--with-top-namespace--returns-dependencies
  (is (= {:ns-path "path"
          :depends-on-interface "file"
          :depends-on-ns "interface"}
         (core/dependency "polylith." "common" "path" #{"spec" "cmd" "file" "common"}
                          "polylith.file.interface"))))

(deftest brick-ns-dependencies--with-top-namespace--returns-dependencies
  (is (= [{:depends-on-interface "cmd"
           :depends-on-ns        "core"
           :ns-path              "/common/core.clj"}
          {:depends-on-interface "invoice"
           :depends-on-ns        "core"
           :ns-path              "/common/core.clj"}]
         (core/brick-ns-dependencies "polylith." "common" #{"spec" "cmd" "file" "invoice"}
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
                    :ns-path              "/common/abc.clj"}]
         (core/brick-dependencies "polylith." "common" #{"spec" "cmd" "user" "invoice"}
                                  '[{:ns-path "/common/core.clj"
                                     :imports [clojure.string
                                               polylith.file.interface]}
                                    {:ns-path "/common/abc.clj"
                                     :imports [clojure.core
                                               polylith.user.interface
                                               polylith.cmd.core
                                               polylith.invoice.core]}]))))

(deftest dependencies--without-errors--returns-dependencies-and-errors
  (is (= {:dependencies ["cmd" "file" "invoice" "user"]
          :errors []}
         (core/dependencies "polylith." "common" #{"spec" "cmd" "file" "invoice" "user"}
                            '[{:ns-path "/common:/readimportsfromdisk.clj"
                               :imports [clojure.string
                                         polylith.file.interface]}
                              {:ns-path "/common/abc.clj"
                               :imports [clojure.core
                                         polylith.user.interface
                                         polylith.cmd.interface.v2.core
                                         polylith.invoice.interface]}]))))

(deftest dependencies--with-errors--returns-dependencies-and-errors
  (is (= {:dependencies ["cmd" "file" "invoice" "user"]
          :errors       ["Illegal dependency on namespace 'invoice.core' in 'components/common/purchase.clj'. Change to 'invoice.interface' to solve the problem."
                         "Illegal dependency on namespace 'cmd.core' in 'components/common/billing.clj'. Change to 'cmd.interface' to solve the problem."]}
         (core/dependencies "polylith." "common" #{"spec" "cmd" "file" "invoice" "user"}
                            '[{:ns-path "/common/purchase.clj"
                               :imports [clojure.string
                                         polylith.file.interface
                                         polylith.invoice.core]}
                              {:ns-path "/common/billing.clj"
                               :imports [clojure.core
                                         polylith.user.interface
                                         polylith.cmd.core]}]))))
