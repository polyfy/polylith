(ns polylith.clj.core.workspace.alias-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.alias :as alias]))

(def environments [{:name "car"}
                   {:name "clojure"}
                   {:name "backend-system"}
                   {:name "banking-system"}
                   {:name "helpers"}])

(deftest anv->alias--a-set-of-environments-without-env-mapping--returns-dev-and-undefined-mappings
  (is (= {"backend-system" "?4"
          "banking-system" "?2"
          "car"            "?3"
          "clojure"        "?1"
          "development"    "dev"
          "helpers"        "?5"}
         (alias/env-to-alias nil environments))))

(deftest env-to-alias--a-set-of-environments-with-incomplete-env-mapping--returns-dev-and-undefined-mappings
  (is (= {"backend-system" "?4"
          "banking-system" "?2"
          "car"            "?3"
          "clojure"        "?1"
          "development"    "dev"
          "helpers"        "h"}
         (alias/env-to-alias {:env-to-alias {"helpers" "h"}} environments))))
