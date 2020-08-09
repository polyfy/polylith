(ns polylith.clj.core.change.to-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.to-test :as to-test]))

(def environments [{:name "development"
                    :has-test-dir? true
                    :component-names ["article"]
                    :test-base-names ["rest-api"]
                    :test-component-names ["article" "comment" "profile" "tag" "user"]}
                   {:name "realworld-backend"
                    :has-test-dir? true
                    :component-names ["article"]
                    :test-base-names ["rest-api"]
                    :test-component-names ["article" "comment" "profile" "tag" "user"]}])

(deftest env->bricks-to-test--with-one-changed-brick--return-included-environments
  (is (= {"development" ["article"]
          "realworld-backend" ["article"]}
         (to-test/env->bricks-to-test environments ["article"] [] {}))))

(deftest environments-to-test--with-one-changed-brick--return-included-environments
  (is (= ["development"
          "realworld-backend"]
         (to-test/environments-to-test environments ["article"] []))))
