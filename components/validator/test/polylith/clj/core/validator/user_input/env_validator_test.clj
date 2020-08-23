(ns polylith.clj.core.validator.user-input.env-validator-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.validator.user-input.env-validator :as validator]))

(def environments [{:alias "adm"}
                   {:name "default"}])

(deftest validate--find-by-alias--returns-with-no-error
  (is (= nil
         (validator/validate #{"adm"} environments color/none))))

(deftest validate--find-by-name--returns-with-no-error
  (is (= nil
         (validator/validate #{"default"} environments color/none))))

(deftest validate--cant-find--returns-with-error
  (is (= ["  Can't find environment: core"]
         (validator/validate #{"core"} environments color/none))))
