(ns polylith.core.test-dependencies
  (:require [clojure.test :refer :all]
            [polylith.core.dependencies :as core]))

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
  (is (= [{:ns-path "/common/readimportsfromdisk.clj"
           :depends-on-interface "file"
           :depends-on-ns "interface"}]
         (core/brick-ns-dependencies "polylith." "common" #{"spec" "cmd" "file" "common"}
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
         (core/brick-dependencies "polylith." "common" #{"spec" "cmd" "file" "common" "user"}
                                  '[{:ns-path "/common/readimportsfromdisk.clj"
                                     :imports [clojure.string polylith.file.interface]}
                                    {:ns-path "/common/abc.clj"
                                     :imports [polylith.user.interface]}]))))

(deftest dependencies
  (is (= {:interfaces ["cmd" "file" "user"]
          :illegal-deps [{:ns-path "/common/abc.clj"
                          :depends-on-interface "cmd"
                          :depends-on-ns "core"}]}
         (core/dependencies "polylith." "common" #{"spec" "cmd" "file" "common" "user"}
                            '[{:ns-path "/common/readimportsfromdisk.clj"
                               :imports [clojure.string polylith.file.interface]}
                              {:ns-path "/common/abc.clj"
                               :imports [polylith.user.interface polylith.cmd.core]}]))))
