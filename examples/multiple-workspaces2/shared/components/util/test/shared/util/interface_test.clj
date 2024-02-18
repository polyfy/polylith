(ns shared.util.interface-test
  (:require [clojure.test :refer :all]
            [shared.util.interface :as util]))

(deftest with-exclamation-mark-test
  (is (= (util/with-exclamation-mark "Hello")
         "Hello!")))
