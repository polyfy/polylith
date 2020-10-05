(ns polylith.clj.core.validator.m207-non-top-namespace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as c]
            [polylith.clj.core.validator.m207-non-top-namespace :as m207]))

(def components [{:name "address"
                  :non-top-namespaces [{:non-top-ns "se.wrong"
                                        :file "components/address/src/se/wrong/address/interface.clj"}
                                       {:non-top-ns "se.wrong"
                                        :file "components/address/src/se/wrong/address/core.clj"}]}])

(deftest warning--when-having-non-top-namespaces--return-warning
  (is (= [{:type "warning"
           :code 207
           :message "Non top namespace se.wrong was found in address."
           :colorized-message "Non top namespace se.wrong was found in address."}]
         (m207/warnings components [] c/none))))
