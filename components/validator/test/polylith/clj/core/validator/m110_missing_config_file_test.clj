(ns polylith.clj.core.validator.m110-missing-config-file-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.m110-invalid-config-file :as m110]))

(deftest errors--in-workspace
  (is (= [{:type "error",
           :code 110,
           :message "Missing config file: deps.edn",
           :colorized-message "Missing config file: deps.edn"}]
         (m110/errors [{:error "Missing config file: deps.edn"}]))))
