(ns polylith.workspace.dependencies-test
  (:require [clojure.test :refer :all]
            [polylith.workspace.dependencies :as deps]))

(deftest dependency--without-top-namespace--returns-dependencies
  (is (= {:ns-path "path"
          :depends-on-interface "file"
          :depends-on-ns "interface"}
         (deps/dependency "" "common" "path" #{"spec" "cmd" "file" "common"}
                          "file.interface"))))

(deftest dependency--with-top-namespace--returns-dependencies
  (is (= {:ns-path "path"
          :depends-on-interface "file"
          :depends-on-ns "interface"}
         (deps/dependency "polylith." "common" "path" #{"spec" "cmd" "file" "common"}
                          "polylith.file.interface"))))

(deftest brick-ns-dependencies--with-top-namespace--returns-dependencies
  (is (= [{:depends-on-interface "cmd"
           :depends-on-ns        "core"
           :ns-path              "/common/core.clj"}
          {:depends-on-interface "invoice"
           :depends-on-ns        "core"
           :ns-path              "/common/core.clj"}]
         (deps/brick-ns-dependencies "polylith." "common" #{"spec" "cmd" "file" "invoice"}
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
         (deps/brick-dependencies "polylith." "common" #{"spec" "cmd" "user" "invoice"}
                                  '[{:ns-path "/common/core.clj"
                                     :imports [clojure.string
                                               polylith.file.interface]}
                                    {:ns-path "/common/abc.clj"
                                     :imports [clojure.core
                                               polylith.user.interface
                                               polylith.cmd.core
                                               polylith.invoice.core]}]))))
