(ns polylith.clj.core.change.environments-test-data
  (:require [clojure.test :refer :all]))

(def environments [{:name "development"
                    :is-dev true
                    :run-tests? false
                    :has-test-dir? true
                    :component-names ["article"]
                    :test-base-names ["rest-api"]
                    :test-component-names ["article" "comment" "profile" "tag" "user"]}
                   {:name "core"
                    :is-dev false
                    :run-tests? true
                    :has-test-dir? true
                    :component-names ["profile"]
                    :test-base-names ["rest-api"]
                    :test-component-names ["article" "comment" "profile" "tag" "user"]}
                   {:name "cli"
                    :is-dev false
                    :run-tests? false
                    :has-test-dir? true
                    :component-names ["article"]
                    :test-base-names ["rest-api"]
                    :test-component-names ["article" "comment" "profile" "tag" "user"]}])
