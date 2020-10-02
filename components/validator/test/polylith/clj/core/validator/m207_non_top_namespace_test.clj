(ns polylith.clj.core.validator.m207-non-top-namespace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as c]
            [polylith.clj.core.validator.m207-non-top-namespace :as m207]))

(deftest warning--when-having-non-top-namespaces--return-warning
  (is (= [{:type "warning"
           :code 207
           :message "Non top namespace se.wrong was found in address."
           :colorized-message "Non top namespace se.wrong was found in address."}]
         (m207/warnings [{:name "address"
                          :type "component"
                          :non-top-ns "se.wrong"
                          :file "components/address/src/se/wrong/address/interface.clj"}
                         {:name "address"
                          :type "component"
                          :non-top-ns "se.wrong"
                          :file "components/address/src/se/wrong/address/core.clj"}] c/none))))
