(ns com.localdep.util.interface-test
  (:require [clojure.test :refer :all]
            [com.localdep.util.interface :as util]))

(deftest date-midnight-2021
  (is (= "2021-01-01T00:00:00.000Z"
         (str (util/date-midnight-2021)))))
