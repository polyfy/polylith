(ns polylith.clj.core.validator.m108-environment-with-multi-implementing-component-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m108-environment-with-multi-implementing-component :as m108]))

(def interfaces [{:name "user"
                  :implementing-components ["admin" "user"]}])

(def environments [{:alias "dev"
                    :src-paths ["development/src"
                                "components/user/src"]
                    :test-paths ["components/user/test"]}])

(deftest errors--when-then-workspace-contains-a-multi-implementing-component--returns-an-error-message
  (is (= [{:type "error"
           :code 108
           :message "Components with an interface that is implemented by more than one component is not allowed for the development environment. They should be added to development profiles instead: user",
           :colorized-message "Components with an interface that is implemented by more than one component is not allowed for the development environment. They should be added to development profiles instead: user"}]
         (m108/errors interfaces environments [] color/none))))
