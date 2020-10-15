(ns polylith.clj.core.change.projects-test-data
  (:require [clojure.test :refer :all]))

(def projects [{:name  "development"
                :is-dev true
                :is-run-tests false
                :has-test-dir? true
                :component-names ["article"]
                :test-base-names ["rest-api"]
                :test-component-names ["article" "comment" "profile" "tag" "user"]}
               {:name "core"
                :is-dev false
                :is-run-tests true
                :has-test-dir? true
                :component-names ["profile"]
                :test-base-names ["rest-api"]
                :test-component-names ["article" "comment" "profile" "tag" "user"]}
               {:name "cli"
                :is-dev false
                :is-run-tests false
                :has-test-dir? true
                :component-names ["article"]
                :test-base-names ["rest-api"]
                :test-component-names ["article" "comment" "profile" "tag" "user"]}])
