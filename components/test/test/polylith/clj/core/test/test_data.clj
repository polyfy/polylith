(ns polylith.clj.core.test.test-data
  (:require [clojure.test :refer :all]))

(def dev {:is-dev true
          :alias "dev"
          :name  "development"
          :deps {"rest-api" {:test {:direct {:src ["article" "comment" "profile" "tag" "user"]}}}}
          :component-names {:src ["article" "comment" "profile" "tag" "user"]
                            :test ["article" "comment" "profile" "tag" "user"]}
          :base-names {:src ["rest-api"]
                       :test ["rest-api"]}})

(def core {:is-dev false
           :alias "core"
           :name "core"
           :component-names {:src ["article" "comment" "tag" "user"]
                             :test ["article" "comment" "tag" "user"]}
           :base-names {:src ["rest-api"]
                        :test ["rest-api"]}})

(defn projects [test]
  [dev
   (assoc core :test test)])
