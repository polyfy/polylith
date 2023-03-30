(ns polylith.clj.core.validator.m108-project-with-multi-implementing-component-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m108-project-with-multi-implementing-component :as m108]))

(def interfaces [{:name "user"
                  :implementing-components ["admin" "user"]}])

(def projects [{:alias      "dev"
                :unmerged   {:paths {:src ["development/src"
                                           "components/user/src"]
                                     :test ["components/user/test"]}}}])

(deftest errors--when-the-development-project-contains-multi-implementing-component--return-an-error-message
  (is (= (m108/errors interfaces projects [] color/none)
         [{:type "error"
           :code 108
           :message "Components with an interface that is implemented by more than one component are not allowed for the development project. They should be added to development profiles instead: user",
           :colorized-message "Components with an interface that is implemented by more than one component are not allowed for the development project. They should be added to development profiles instead: user"}])))
