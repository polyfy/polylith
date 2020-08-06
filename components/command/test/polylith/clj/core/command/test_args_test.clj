(ns polylith.clj.core.command.test-args-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.command.test-args :as test-args]))

(deftest args--without-env-and-without-args--returns-expected-map
  (is (= {:env nil
          :run-all? false
          :run-env-tests? false}
         (test-args/args nil nil))))

(deftest args--with-env-and-without-args--returns-expected-map
  (is (= {:env "core"
          :run-all? false
          :run-env-tests? false}
         (test-args/args "core" nil))))

(deftest args--with-env-and-all-args--returns-expected-map
  (is (= {:env "core"
          :run-all? true
          :run-env-tests? true}
         (test-args/args "core" "-all"))))

(deftest args--with-env-and-all-bricks-args--returns-expected-map
  (is (= {:env "core"
          :run-all? true
          :run-env-tests? false}
         (test-args/args "core" "-all-bricks"))))

(deftest args--with-env-and-all-bricks-args--returns-expected-map
  (is (= {:env "core"
          :run-all? false
          :run-env-tests? true}
         (test-args/args "core" "-env"))))
