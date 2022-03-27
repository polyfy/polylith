(ns polylith.clj.core.change.test-data
  (:require [clojure.test :refer :all]))

(def projects [{:is-dev true
                :alias "dev"
                :name  "development"
                :component-names {:test ["article" "comment" "profile" "tag" "user"]}
                :base-names {:test ["rest-api"]}}

               {:is-dev false
                :alias "core"
                :name "core"
                :component-names {:test ["article" "comment" "tag" "user"]}
                :base-names {:test ["rest-api"]}}])
