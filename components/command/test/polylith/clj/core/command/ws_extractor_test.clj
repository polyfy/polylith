(ns polylith.clj.core.command.ws-extractor-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.command.ws-extractor :as ws]))

(def workspace {:environments [{:name "development"
                                :alias "dev"
                                :type  "environment"}]
                "a-key" 123})

(deftest ws--extract-with-no-arguments
  (is (= workspace
         (ws/extract workspace []))))

(deftest ws--extract-element-by-index
  (is (= "environment"
         (ws/extract workspace ["environments" "0" "type"]))))

(deftest ws--extract-element-by-name
  (is (= "development"
         (ws/extract workspace ["environments" "development" "name"]))))

(deftest ws--extract-element-by-name
  (is (= "dev"
         (ws/extract workspace ["environments" "dev" "alias"]))))

(deftest ws--extract-string-key
  (is (= 123
         (ws/extract workspace ["a-key"]))))

(deftest ws--extract-keys
  (is (= ["a-key"
          "environments"]
         (ws/extract workspace ["keys"]))))

(deftest ws--extract-keys-in-list-of-maps
  (is (= ["development"]
         (ws/extract workspace ["environments" "keys"]))))
