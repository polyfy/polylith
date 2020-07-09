(ns polylith.clj.core.workspace.alias-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.alias :as alias]))

(def environments [{:name "backend-system"}
                   {:name "banking-system"}
                   {:name "helpers"}])

(deftest anv->alias--a-set-of-environments-without-env-mapping--returns-name-to-alias-map
  (is (= {"backend-system" "bs1",
          "banking-system" "bs2",
          "helpers" "h",}
         (alias/env->alias nil environments))))

(deftest env->alias--a-set-of-environments-with-incomplete-env-mapping--returns-mapped-names-and-undefined-mappings
  (is (= {"helpers" "h", "banking-system" "?1", "backend-system" "?2"}
         (alias/env->alias {:env-short-names {"helpers" "h"}} environments))))
