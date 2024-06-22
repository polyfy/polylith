(ns se.example.user.interface-test
  (:require [clojure.test :as test :refer :all]
            [se.example.user.interface :as user]
            [se.example.database.interface :as database]
            [se.example.test-helper.interface :as test-helper]))

(deftest do-stuff
  (test-helper/do-stuff))
