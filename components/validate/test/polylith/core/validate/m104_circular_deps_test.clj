(ns polylith.core.validate.m104-circular-deps-test
  (:require [clojure.test :refer :all]
            [polylith.core.util.interfc.color :as color]
            [polylith.core.validate.m104-circular-deps :as m104]))

(def interfaces [{:name "article"}
                 {:name "comment"}
                 {:name "database"}
                 {:name "log"}
                 {:name "profile"}
                 {:name "spec"}
                 {:name "tag"}
                 {:name "user"}])

(def components [{:name "article"
                  :interface {:name "article"}
                  :interface-deps ["database" "profile" "spec"]}
                 {:name "article2"
                  :interface {:name "article"}
                  :interface-deps []}
                 {:name "comment"
                  :interface {:name "comment"}
                  :interface-deps ["article" "database" "profile" "spec"]}
                 {:name "database"
                  :interface {:name "database"}
                  :interface-deps ["article" "log"]}
                 {:name "log"
                  :interface {:name "log"}
                  :interface-deps []}
                 {:name "profile"
                  :interface {:name "profile"}
                  :interface-deps ["article" "database" "spec" "user"]}
                 {:name "spec"
                  :interface {:name "spec"}
                  :interface-deps []}
                 {:name "tag"
                  :interface {:name "tag"}
                  :interface-deps ["database"]}
                 {:name "user"
                  :interface {:name "user"}
                  :interface-deps ["database" "spec"]}])

(def environments [{:name "build-tools"
                    :component-names []}
                   {:name "build-tools-test"
                    :component-names []}
                   {:name "realworld-backend"
                    :component-names ["article" "comment" "database" "log" "profile" "spec" "tag" "user"]}
                   {:name "realworld-backend-test"
                    :component-names ["article" "comment" "database" "log" "profile" "spec" "tag" "user"]}])

(def components2 [{:name "article"
                   :interface {:name "article"}
                   :interface-deps ["database" "profile" "spec"]}
                  {:name "article2"
                   :interface {:name "article"}
                   :interface-deps []}
                  {:name "comment"
                   :interface {:name "comment"}
                   :interface-deps ["article" "database" "profile" "spec"]}])

(def environments2 [{:name "build-tools"
                     :component-names ["article" "article2" "comment"]}])

(deftest errors--an-environment-with-circular-dependencies--should-return-an-error
  (is (= [{:type "error"
           :code 104
           :colorized-message "A circular dependency was found in the realworld-backend environment: article > database > article"
           :message           "A circular dependency was found in the realworld-backend environment: article > database > article"
           :components ["article" "database"]
           :environment "realworld-backend"}]
         (m104/errors interfaces components environments color/none))))

(deftest errors--when-having-no-environments--return-no-errors
  (is (= nil
         (m104/errors interfaces components [] color/none))))

(deftest errors--when-having-two-compoments-implementing-the-same-interface--do-not-crash
  (is (= nil
         (m104/errors interfaces components2 environments2 color/none))))
