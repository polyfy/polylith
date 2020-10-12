(ns polylith.clj.core.command.cmd-validator.environment-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.command.cmd-validator.environment :as environment]))

(def environments [{:alias "adm"}
                   {:name "default"}])

(deftest validate--find-by-alias--returns-with-no-error
  (is (= nil
         (environment/validate #{"adm"} environments color/none))))

(deftest validate--find-by-name--returns-with-no-error
  (is (= nil
         (environment/validate #{"default"} environments color/none))))

(deftest validate--cant-find--returns-with-error
  (is (= ["  Can't find environment: core"]
         (environment/validate #{"core"} environments color/none))))
