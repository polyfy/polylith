(ns polylith.clj.core.workspace.alias-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.alias :as alias]))

(def projects [{:name "car"}
               {:name "clojure"}
               {:name "backend-system"}
               {:name "banking-system"}
               {:name "helpers"}])

(deftest project-toalias--a-set-of-projects-without-project-mapping--returns-dev-and-undefined-mappings
  (is (= {"backend-system" "?4"
          "banking-system" "?2"
          "car"            "?3"
          "clojure"        "?1"
          "development"    "dev"
          "helpers"        "?5"}
         (alias/project-to-alias nil projects))))

(deftest project-to-alias--a-set-of-projects-with-incomplete-project-mapping--returns-dev-and-undefined-mappings
  (is (= {"backend-system" "?4"
          "banking-system" "?2"
          "car"            "?3"
          "clojure"        "?1"
          "development"    "dev"
          "helpers"        "h"}
         (alias/project-to-alias {:project-to-alias {"helpers" "h"}} projects))))
