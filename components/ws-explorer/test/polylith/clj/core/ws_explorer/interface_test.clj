(ns polylith.clj.core.ws-explorer.interface-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.ws-explorer.interface :as ws]))

(def workspace {:projects [{:name "development"
                            :alias "dev"
                            :type  "project"}]
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

(deftest ws--extract-keys-in-list-of-maps
  (is (= ["development"]
         (ws/extract workspace ["projects" "keys"]))))

(deftest ws--count
  (is (= 2
         (ws/extract workspace ["count"]))))
