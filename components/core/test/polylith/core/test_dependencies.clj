(ns polylith.core.test-dependencies
  (:require [clojure.test :refer :all]
            [polylith.common.dependencies :as deps]))

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
  (is (= [{:ns-path "/common/readimportsfromdisk.clj"
           :depends-on-interface "file"
           :depends-on-ns "interface"}]
         (deps/brick-ns-dependencies "polylith." "common" #{"spec" "cmd" "file" "common"}
                                     '{:ns-path "/common/readimportsfromdisk.clj"
                                       :imports [clojure.string
                                                 polylith.file.interface]}))))

(deftest brick-dependencies--with-top-namespace--returns-dependencies
  (is (= [{:depends-on-interface "file"
           :depends-on-ns        "interface"
           :ns-path              "/common/readimportsfromdisk.clj"}
          {:depends-on-interface "user"
           :depends-on-ns        "interface"
           :ns-path              "/common/abc.clj"}]
         (deps/brick-dependencies "polylith." "common" #{"spec" "cmd" "file" "common" "user"}
                                  '[{:ns-path "/common/readimportsfromdisk.clj"
                                     :imports [clojure.string polylith.file.interface]}
                                    {:ns-path "/common/abc.clj"
                                     :imports [polylith.user.interface]}]))))

(deftest dependencies
  (is (= {:interfaces ["cmd" "file" "user"]
          :illegal-deps [{:ns-path "/common/abc.clj"
                          :depends-on-interface "cmd"
                          :depends-on-ns "core"}]}
         (deps/dependencies "polylith." "common" #{"spec" "cmd" "file" "common" "user"}
                            '[{:ns-path "/common/readimportsfromdisk.clj"
                               :imports [clojure.string polylith.file.interface]}
                              {:ns-path "/common/abc.clj"
                               :imports [polylith.user.interface polylith.cmd.core]}]))))
