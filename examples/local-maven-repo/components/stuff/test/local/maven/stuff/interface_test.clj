(ns local.maven.stuff.interface-test
  (:require [clojure.test :refer :all]
            [local.maven.stuff.interface :as stuff]))

(deftest test-stuff
  (is (= 3.14
         stuff/pi)))
