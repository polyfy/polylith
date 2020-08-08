(ns polylith.clj.core.command.test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.command.test :as tst]))

(deftest args--with-nothing-selected--returns-expected-map
  (is (= {:run-all? false
          :run-env-tests? false}
         (tst/args nil nil nil))))

(deftest args--with-run-all-selected--returns-expected-map
  (is (= {:run-all? true
          :run-env-tests? true}
         (tst/args nil "true" nil))))

(deftest args--with-run-all-bricks-selected--returns-expected-map
  (is (= {:run-all? true
          :run-env-tests? false}
         (tst/args nil nil "true"))))

(deftest args--with-env-selected--returns-expected-map
  (is (= {:run-all? false
          :run-env-tests? true}
         (tst/args "true" nil nil))))
