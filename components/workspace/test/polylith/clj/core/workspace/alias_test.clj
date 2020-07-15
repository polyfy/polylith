(ns polylith.clj.core.workspace.alias-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.alias :as alias]))

(def environments [{:name "car"}
                   {:name "clojure"}
                   {:name "backend-system"}
                   {:name "banking-system"}
                   {:name "helpers"}])

(deftest anv->alias--a-set-of-environments-without-env-mapping--returns-name-to-alias-map
  (is (= {"backend-system" "bas1"
          "banking-system" "bas2"
          "car"            "ca"
          "clojure"        "cl"
          "helpers"        "he"}
         (alias/env->alias nil environments))))

(deftest env->alias--a-set-of-environments-with-incomplete-env-mapping--returns-mapped-names-and-undefined-mappings
  (is (= {"backend-system" "?4"
          "banking-system" "?2"
          "car"            "?3"
          "clojure"        "?1"
          "helpers"        "h"}
         (alias/env->alias {:env-short-names {"helpers" "h"}} environments))))
