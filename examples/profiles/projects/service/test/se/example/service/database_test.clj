(ns se.example.service.database-test
  (:require
   [clojure.test :refer [deftest is]]
   [se.example.database.interface :as db]))

(deftest whoami-test
  (is (= "always wrong"
         (db/whoami))))
