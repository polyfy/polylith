(ns polylith.clj.core.change.test-data
  (:require [clojure.test :refer :all]))

(def projects [{:name  "development"
                :is-dev true
                :is-run-tests false
                :has-test-dir? true
                :component-names {:src ["article"]
                                  :test ["article" "comment" "profile" "tag" "user"]}
                :base-names {:test ["rest-api"]}}
               {:name "core"
                :is-dev false
                :is-run-tests true
                :has-test-dir? true
                :component-names {:src ["profile"]
                                  :test ["article" "comment" "profile" "tag" "user"]}
                :base-names {:test ["rest-api"]}
                :test-paths ["projects/core/test"]}
               {:name "cli"
                :is-dev false
                :is-run-tests false
                :has-test-dir? true
                :component-names {:src ["article"]
                                  :test ["article" "comment" "profile" "tag" "user"]}
                :base-names {:test ["rest-api"]}}])
