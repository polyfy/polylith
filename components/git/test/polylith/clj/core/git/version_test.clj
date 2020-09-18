(ns polylith.clj.core.git.version-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.git.version :as version]))

(deftest version--matching-pattern--returns-true
  (is (= true
         (version/version? "v*" "v1.2.3"))))

(deftest version--mismatching-pattern--returns-false
  (is (= false
         (version/version? "v*" "version1.2.3"))))
