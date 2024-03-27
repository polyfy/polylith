(ns polylith.clj.core.deps.ws-interface-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.interface-deps.ws-deps :as ws-deps]))

(deftest dependency--without-top-namespace--returns-dependencies
  (is (= {:namespace "ns"
          :depends-on-interface "file"
          :depends-on-ns "interface"}
         (ws-deps/dependency "" "common" "ns" #{"spec" "cmd" "file" "common"}
                             "file.interface"))))

(deftest dependency--with-top-namespace--returns-dependencies
  (is (= {:namespace "ns"
          :depends-on-interface "file"
          :depends-on-ns "interface"}
         (ws-deps/dependency "polylith." "common" "ns" #{"spec" "cmd" "file" "common"}
                             "polylith.file.interface"))))

(deftest interface-ns-import-deps--with-top-namespace--returns-dependencies
  (is (= [{:namespace "core.clj"
           :depends-on-interface "cmd"
           :depends-on-ns "core"}
          {:namespace "core.clj"
           :depends-on-interface "invoice"
           :depends-on-ns "core"}]
         (ws-deps/interface-ns-import-deps "polylith." "common" #{"spec" "cmd" "file" "invoice"}
                                           '{:name "core.clj"
                                             :imports ["clojure.core"
                                                       "polylith.user.interface"
                                                       "polylith.cmd.core"
                                                       "polylith.invoice.core"]}))))

(deftest interface-ns-deps--with-top-namespace--returns-dependencies
  (is (= [{:namespace "abc.clj"
           :depends-on-interface "user"
           :depends-on-ns "interface"}
          {:namespace "abc.clj"
           :depends-on-interface "cmd"
           :depends-on-ns "core"}
          {:namespace "abc.clj"
           :depends-on-interface "invoice"
           :depends-on-ns "core"}]
         (ws-deps/interface-ns-deps "polylith." "common" #{"spec" "cmd" "user" "invoice"}
                                    '[{:name "core.clj"
                                       :imports ["clojure.string"
                                                 "polylith.file.interface"]}
                                      {:name "abc.clj"
                                       :imports ["clojure.core"
                                                 "polylith.user.interface"
                                                 "polylith.cmd.core"
                                                 "polylith.invoice.core"]}]))))
