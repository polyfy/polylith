(ns polylith.clj.core.poly-cli.api-argument-mapping-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.poly-cli.api :as api]))

(deftest boolean-value
  (is (= [":loc"]
         (api/argument-mapping {"loc" true}))))

(deftest entity-as-string
  (is (= ["workspace"]
         (api/argument-mapping {"entity" "workspace"}))))

(deftest entity-as-symbol
  (is (= ["workspace"]
         (api/argument-mapping {"entity" 'workspace}))))

(deftest profiles
  (is (= ["+a" "+b"]
         (api/argument-mapping {"profiles" '[a b]}))))
