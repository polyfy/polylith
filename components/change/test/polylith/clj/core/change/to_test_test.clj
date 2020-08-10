(ns polylith.clj.core.change.to-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.to-test :as to-test]))

(def environments [{:name "development"
                    :dev? true
                    :has-test-dir? true
                    :component-names ["article"]
                    :test-base-names ["rest-api"]
                    :test-component-names ["article" "comment" "profile" "tag" "user"]}
                   {:name "realworld-backend"
                    :dev? false
                    :has-test-dir? true
                    :component-names ["article"]
                    :test-base-names ["rest-api"]
                    :test-component-names ["article" "comment" "profile" "tag" "user"]}])

(deftest env->bricks-to-test--with-one-changed-brick-exclude-dev--return-included-environments2
  (is (= {"development" []
          "realworld-backend" ["article"]}
         (to-test/env->bricks-to-test environments ["article"] [] {} false))))

(deftest env->bricks-to-test--with-one-changed-brick-include-dev--return-included-environments
  (is (= {"development" ["article"]
          "realworld-backend" ["article"]}
         (to-test/env->bricks-to-test environments ["article"] [] {} true))))

(deftest environments-to-test--with-one-changed-brick--return-included-environments
  (is (= ["development"
          "realworld-backend"]
         (to-test/environments-to-test environments ["article"] []))))
