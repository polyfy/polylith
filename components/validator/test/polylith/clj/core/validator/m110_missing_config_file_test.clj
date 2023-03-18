(ns polylith.clj.core.validator.m110-missing-config-file-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.m110-missing-config-file :as m110]))

(deftest errors--missing-config-file
  (is (= (m110/errors [{:error "Missing config file: deps.edn"}])
         [{:code              110
           :colorized-message "Missing config file: deps.edn"
           :message           "Missing config file: deps.edn"
           :type              "error"}])))
