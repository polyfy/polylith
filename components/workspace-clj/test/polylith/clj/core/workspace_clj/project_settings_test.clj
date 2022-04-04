(ns polylith.clj.core.workspace-clj.project-settings-test
  (:require
   [clojure.test :refer :all]
   [polylith.clj.core.workspace-clj.project-settings :as sut]))

(deftest no-projects-no-project-settings
  (is (= {} (sut/convert {}))))

(deftest simple-project
  (is (= {"foo" {}}
         (sut/convert {:projects {"foo" {}}}))))

(deftest legacy-test-vector
  (is (= {"foo" {:test {:include ["bar"]}}}
         (sut/convert {:projects {"foo" {:test ["bar"]}}}))))

(deftest global-test-spec
  (is (= {"foo" {:test {:overridden :with-project
                        :from-project :from-project
                        :untouched :at-top}}}
         (sut/convert {:projects {"foo" {:test {:overridden :with-project
                                                :from-project :from-project}}}
                       :test {:overridden :not
                              :untouched :at-top}}))))

(deftest legacy-project+global-settings
  (is (= {"foo" {:test {:include ["bar"]
                        :from-top :from-top}}}
         (sut/convert {:projects {"foo" {:test ["bar"]}}
                       :test {:from-top :from-top}}))))
