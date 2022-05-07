(ns polylith.clj.core.workspace-clj.project-settings-test
  (:require
   [clojure.test :refer :all]
   [polylith.clj.core.workspace-clj.project-settings :as sut]))

(def default-test-runner-constructor
  'polylith.clj.core.clojure-test-test-runner.interface/create)

(deftest no-projects-no-project-settings
  (is (= {} (sut/convert {}))))

(deftest simple-project
  (is (= {"foo" {:test {:create-test-runner [default-test-runner-constructor]}}}
         (sut/convert {:projects {"foo" {}}}))))

(deftest legacy-test-vector
  (is (= {"foo" {:test {:include ["bar"]
                        :create-test-runner [default-test-runner-constructor]}}}
         (sut/convert {:projects {"foo" {:test ["bar"]}}}))))

(deftest global-test-spec
  (is (= {"foo" {:test {:overridden :with-project
                        :from-project :from-project
                        :untouched :at-top
                        :create-test-runner [default-test-runner-constructor]}}}
         (sut/convert
          {:projects {"foo" {:test {:overridden :with-project
                                    :from-project :from-project}}}
           :test {:overridden :not
                  :untouched :at-top}}))))

(deftest legacy-project+global-settings
  (is (= {"foo" {:test {:include ["bar"]
                        :from-top :from-top
                        :create-test-runner [default-test-runner-constructor]}}}
         (sut/convert
          {:projects {"foo" {:test ["bar"]}}
           :test {:from-top :from-top}}))))

(deftest test-runner-constructor-without-top-level
  (is (= {"foo" {:test {:create-test-runner [default-test-runner-constructor]}}
          "bar" {:test {:create-test-runner ['my.ns/create]}}
          "baz" {:test {:create-test-runner [default-test-runner-constructor]}}}
         (sut/convert
          {:projects {"foo" {}
                      "bar" {:test {:create-test-runner 'my.ns/create}}
                      "baz" {:test {:create-test-runner :default}}}}))))

(deftest test-runner-constructor-with-top-level
  (is (= {"foo" {:test {:create-test-runner ['global.test/create]}}
          "bar" {:test {:create-test-runner ['my.ns/create]}}
          "baz" {:test {:create-test-runner [default-test-runner-constructor]}}}
         (sut/convert
          {:test {:create-test-runner 'global.test/create}
           :projects {"foo" {}
                      "bar" {:test {:create-test-runner 'my.ns/create}}
                      "baz" {:test {:create-test-runner :default}}}}))))

(deftest multiple-test-runner-constructors
  (is (= {"foo" {:test {:create-test-runner ['global.test/create default-test-runner-constructor]}}
          "bar" {:test {:create-test-runner ['my.ns/create]}}
          "baz" {:test {:create-test-runner [default-test-runner-constructor]}}
          "qux" {:test {:create-test-runner ['other.ns/create
                                             default-test-runner-constructor
                                             'global.test/create]}}}
         (sut/convert
          {:test {:create-test-runner ['global.test/create :default]}
           :projects {"foo" {}
                      "bar" {:test {:create-test-runner 'my.ns/create}}
                      "baz" {:test {:create-test-runner :default}}
                      "qux" {:test {:create-test-runner ['other.ns/create
                                                         :default
                                                         'global.test/create]}}}}))))
