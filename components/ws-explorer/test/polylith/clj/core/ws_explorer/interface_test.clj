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
  (is (= (ws/extract workspace [])
         workspace)))

(deftest ws--extract-element-by-index
  (is (= (ws/extract workspace ["projects" "0" "type"])
         "project")))

(deftest ws--extract-element-by-name
  (is (= (ws/extract workspace ["projects" "development" "name"])
         "development")))

(deftest ws--extract-element-by-name
  (is (= (ws/extract workspace ["projects" "dev" "alias"])
         "dev")))

(deftest ws--extract-string-key
  (is (= (ws/extract workspace ["map2" "a-key"])
         123)))

(deftest ws--extract-keys
  (is (= (ws/extract workspace ["keys"])
         [:map2
          :projects])))

(deftest ws--extract-keys-in-list-of-maps-with-keys-keyword
  (is (= (ws/extract workspace ["projects" "keys"])
         ["development" "delivery" "invoicer"])))

(deftest ws--extract-keys-in-list-of-maps-with-empty-key
  (is (= (ws/extract workspace ["projects" ""])
         ["development" "delivery" "invoicer"])))

(deftest ws--count
  (is (= (ws/extract workspace ["count"])
         2)))

(deftest ws--extract-element-in-vector-by-index
  (is (= (ws/extract workspace ["projects" "keys" "2"])
         "invoicer")))

(deftest ws--filter-project-keys
  (is (= (ws/extract workspace ["projects" "de*" "keys"])
         ["development" "delivery"])))

(deftest ws--filter-project-keys
  (is (= (ws/extract {:name "car"
                      :value "hello"} ["ca*" ""])
         [:name :value])))

(deftest ws--filter-project-keys
  (is (= (ws/extract ["adam" "bert" "cecar" "david"] ["c*"])
         ["cecar"])))
