(ns polylith.clj.core.util.date-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.date :as sut]))

(deftest parse-date--date
  (is (= #inst"2025-01-01T00:00:00.000-00:00"
         (sut/parse-date "2025"))))

(deftest parse-date--date
  (is (= #inst"2025-01-15T00:00:00.000-00:00"
         (sut/parse-date "2025-01-15"))))

(deftest parse-date--datetime
  (is (= #inst"2023-03-10T14:25:11.000-00:00"
         (sut/parse-date "2023-03-10T14:25:11Z"))))
