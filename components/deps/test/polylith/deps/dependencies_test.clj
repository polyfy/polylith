(ns polylith.deps.dependencies-test
  (:require [clojure.test :refer :all]
            [polylith.deps.dependencies :as deps]))

(deftest dependency--without-top-namespace--returns-dependencies
  (is (= {:namespace "path"
          :depends-on-interface "file"
          :depends-on-ns "interface"}
         (deps/dependency "" "common" "path" #{"spec" "cmd" "file" "common"}
                          "file.interface"))))

(deftest dependency--with-top-namespace--returns-dependencies
  (is (= {:namespace "path"
          :depends-on-interface "file"
          :depends-on-ns "interface"}
         (deps/dependency "polylith." "common" "path" #{"spec" "cmd" "file" "common"}
                          "polylith.file.interface"))))

(deftest brick-ns-dependencies--with-top-namespace--returns-dependencies
  (is (= [{:namespace "common/core.clj"
           :depends-on-interface "cmd"
           :depends-on-ns "core"}
          {:namespace "common/core.clj"
           :depends-on-interface "invoice"
           :depends-on-ns "core"}]

         (deps/brick-ns-dependencies "polylith." "common" #{"spec" "cmd" "file" "invoice"}
                                     '{:name "common/core.clj"
                                       :imports ["clojure.core"
                                                 "polylith.user.interface"
                                                 "polylith.cmd.core"
                                                 "polylith.invoice.core"]}))))

(deftest brick-dependencies--with-top-namespace--returns-dependencies
  (is (= [{:namespace "common/abc.clj"
           :depends-on-interface "user"
           :depends-on-ns "interface"}
          {:namespace "common/abc.clj"
           :depends-on-interface "cmd"
           :depends-on-ns "core"}
          {:namespace "common/abc.clj"
           :depends-on-interface "invoice"
           :depends-on-ns "core"}]
         (deps/brick-dependencies "polylith." "common" #{"spec" "cmd" "user" "invoice"}
                                  '[{:name "common/core.clj"
                                     :imports ["clojure.string"
                                               "polylith.file.interface"]}
                                    {:name "common/abc.clj"
                                     :imports ["clojure.core"
                                               "polylith.user.interface"
                                               "polylith.cmd.core"
                                               "polylith.invoice.core"]}]))))
