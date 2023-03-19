(ns polylith.clj.core.workspace-clj.project-settings-test
  (:require
   [clojure.test :refer :all]
   [polylith.clj.core.workspace-clj.project-settings :as sut]))

(def default-test-runner-constructor
  'polylith.clj.core.clojure-test-test-runner.interface/create)

(deftest no-projects-no-project-settings
  (is (= (sut/convert {})
         {})))

(deftest simple-project
  (is (= (sut/convert {:projects {"foo" {}}})
         {"foo" {}})))

(deftest legacy-test-vector
  (is (= (sut/convert {:projects {"foo" {:test ["bar"]}}})
         {"foo" {:test {:include ["bar"]}}})))

(deftest global-test-spec
  (is (= (sut/convert
          {:projects {"foo" {:test {:overridden :with-project
                                    :from-project :from-project}}}
           :test {:overridden :not
                  :untouched :at-top}})
         {"foo" {:test {:overridden :with-project
                        :from-project :from-project
                        :untouched :at-top}}})))

(deftest legacy-project+global-settings
  (is (= (sut/convert
          {:projects {"foo" {:test ["bar"]}}
           :test {:from-top :from-top}})
         {"foo" {:test {:include ["bar"]
                        :from-top :from-top}}})))

(deftest test-runner-constructor-without-top-level
  (is (= (sut/convert
          {:projects {"foo" {}
                      "bar" {:test {:create-test-runner 'my.ns/create}}
                      "baz" {:test {:create-test-runner :default}}}})
         {"foo" {}
          "bar" {:test {:create-test-runner 'my.ns/create}}
          "baz" {:test {:create-test-runner :default}}})))

(deftest test-runner-constructor-with-top-level
  (is (= (sut/convert
          {:test {:create-test-runner 'global.test/create}
           :projects {"foo" {}
                      "bar" {:test {:create-test-runner 'my.ns/create}}
                      "baz" {:test {:create-test-runner :default}}}})
         {"foo" {:test {:create-test-runner 'global.test/create}}
          "bar" {:test {:create-test-runner 'my.ns/create}}
          "baz" {:test {:create-test-runner :default}}})))

(deftest multiple-test-runner-constructors
  (is (= (sut/convert
          {:test {:create-test-runner ['global.test/create :default]}
           :projects {"foo" {}
                      "bar" {:test {:create-test-runner 'my.ns/create}}
                      "baz" {:test {:create-test-runner :default}}
                      "qux" {:test {:create-test-runner ['other.ns/create
                                                         :default
                                                         'global.test/create]}}}})
         {"foo" {:test {:create-test-runner ['global.test/create :default]}}
          "bar" {:test {:create-test-runner 'my.ns/create}}
          "baz" {:test {:create-test-runner :default}}
          "qux" {:test {:create-test-runner ['other.ns/create
                                             :default
                                             'global.test/create]}}})))
