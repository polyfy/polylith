(ns polylith.clj.core.validate.m108-environment-with-multi-implementing-component-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validate.m108-environment-with-multi-implementing-component :as m108]
            [polylith.clj.core.util.interfc.color :as color]))

(def interfaces [{:name "user"
                  :implementing-components ["admin" "user"]}])

(def environments [{:name "development"
                    :component-names ["user"]}])

(deftest errors--
  (is (= [{:type "error",
           :code 108,
           :message "Components with an interface that are implemented by more than one component is not allowed for the development environment. They should be added to development profiles instead: user",
           :colorized-message "Components with an interface that are implemented by more than one component is not allowed for the development environment. They should be added to development profiles instead: user"}]
         (m108/errors interfaces environments color/none))))
