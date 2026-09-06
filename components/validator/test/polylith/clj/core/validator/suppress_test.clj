(ns polylith.clj.core.validator.suppress-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.suppress :as sut]))

(deftest suppress-component
  (is (= true
         (sut/suppress? {:warning 205}
                        205 {:type "component", :name "invoice"}))))

(deftest suppress-selected-component
  (is (= true
         (sut/suppress? {:warning 205, :bricks ["invoice"]}
                        205 {:type "component", :name "invoice"}))))

(deftest keep-component
  (is (= false
         (sut/suppress? {:warning 205, :bricks ["invoice"]}
                        200 {:type "component", :name "invoice"}))))

(deftest suppress-base
  (is (= true
         (sut/suppress? {:warning 205}
                        205 {:type "base", :name "cli"}))))

(deftest suppress-project
  (is (= true
         (sut/suppress? {:warning 202}
                        202 {:type "project", :name "cli"}))))

(deftest suppress-selected-project
  (is (= true
         (sut/suppress? {:warning 202, :projects ["p1"]}
                        202 {:type "project", :name "p1"}))))

(deftest dont-suppress-selected-project
  (is (= false
         (sut/suppress? {:warning 202, :projects ["cli"]}
                        202 {:type "project", :name "aaa"}))))

(def discard [{:warning 202, :projects ["p1"]}
              {:error 104, :projects ["p2"]}
              {:warning 205}])

(deftest suppress-error-104-for-project-p2
  (is (= [{:type "project", :name "aaa"}
          {:type "project", :name "p1"}]
         (sut/suppress discard 104
                       [{:type "project", :name "aaa"}
                        {:type "project", :name "p1"}
                        {:type "project", :name "p2"}]))))

(deftest suppress-warning-205
  (is (= []
         (sut/suppress discard 205
                       [{:type "project", :name "aaa"}
                        {:type "project", :name "p2"}]))))
