(ns polylith.clj.core.ws-explorer.interface-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.ws-explorer.interface :as ws]))

(def workspace {:projects [{:name "development"
                            :alias "dev"
                            :type  "project"}
                           {:name "delivery"
                            :alias "del"
                            :type  "project"}
                           {:name "invoicer"
                            :alias "inv"
                            :type "project"}]
                :map2 {"a-key" 123}})

(deftest ws--extract-with-no-arguments
  (is (= workspace
         (ws/extract workspace []))))

(deftest ws--extract-element-by-index
  (is (= "project"
         (ws/extract workspace ["projects" "0" "type"]))))

(deftest ws--extract-element-by-name
  (is (= "development"
         (ws/extract workspace ["projects" "development" "name"]))))

(deftest ws--extract-element-by-name
  (is (= "dev"
         (ws/extract workspace ["projects" "dev" "alias"]))))

(deftest ws--extract-string-key
  (is (= 123
         (ws/extract workspace ["map2" "a-key"]))))

(deftest ws--extract-keys
  (is (= [:map2
          :projects]
         (ws/extract workspace ["keys"]))))

(deftest ws--extract-keys-in-list-of-maps-with-keys-keyword
  (is (= ["development" "delivery" "invoicer"]
         (ws/extract workspace ["projects" "keys"]))))

(deftest ws--extract-keys-in-list-of-maps-with-empty-key
  (is (= ["development" "delivery" "invoicer"]
         (ws/extract workspace ["projects" ""]))))

(deftest ws--count
  (is (= 2
         (ws/extract workspace ["count"]))))

(deftest ws--extract-element-in-vector-by-index
  (is (= "invoicer"
         (ws/extract workspace ["projects" "keys" "2"]))))

(deftest ws--filter-project-keys
  (is (= ["development" "delivery"]
         (ws/extract workspace ["projects" "de*" "keys"]))))

(deftest ws--filter-project-keys
  (is (= [:name :value]
         (ws/extract {:name "car"
                      :value "hello"} ["ca*" ""]))))

(deftest ws--filter-project-keys
  (is (= ["cecar"]
         (ws/extract ["adam" "bert" "cecar" "david"] ["c*"]))))
