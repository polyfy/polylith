(ns polylith.workspace.source-test
  (:require [clojure.test :refer :all]
            [polylith.workspace.source :as source]))

(def workspace '{:ws-path "."
                 :environments [{:name "core"
                                 :group "core"
                                 :test? false
                                 :components [{:name "change", :type "component"}
                                              {:name "cmd", :type "component"}]
                                 :bases [{:name "tool", :type "base"}]
                                 :paths ["components/change/src"
                                         "components/cmd/src"
                                         "bases/cli/src"]
                                 :interface-deps #:org.clojure{clojure #:mvn{:version "1.10.1"}
                                                               core.async #:mvn{:version "0.4.500"}
                                                               tools.deps.alpha #:mvn{:version "0.6.496"}}}
                                {:name "core-test"
                                 :group "core"
                                 :test? true
                                 :components [{:name "change", :type "component"}
                                              {:name "cmd", :type "component"}]
                                 :bases []
                                 :paths ["components/change/test"
                                         "components/cmd/test"],
                                 :interface-deps #:org.clojure{clojure #:mvn{:version "1.10.1"}
                                                               core.async #:mvn{:version "0.4.500"}
                                                               tools.deps.alpha #:mvn{:version "0.6.496"}}}]
                 :messages {:warnings [], :errors []}})

(deftest paths--when-include-test-path-flag-is-false---include-only-src-paths
  (is (= ["./bases/cli/src"
          "./components/change/src"
          "./components/cmd/src"]
         (source/paths workspace "core" false))))

(deftest paths--when-include-test-path-flag-is-true---include-both-src-and-test-paths
  (is (= ["./bases/cli/src"
          "./components/change/src"
          "./components/change/test"
          "./components/cmd/src"
          "./components/cmd/test"]
         (source/paths workspace "core" true))))
