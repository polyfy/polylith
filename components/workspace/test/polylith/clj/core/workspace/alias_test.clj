(ns polylith.clj.core.workspace.alias-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.alias :as alias]))

(def environments [{:name "backend-system"
                    :group "backend-system"
                    :test? false}
                   {:name "backend-system-test"
                    :group "backend-system"
                    :test? true}
                   {:name "banking-system"
                    :group "banking-system"
                    :test? false}
                   {:name "banking-system-test"
                    :group "banking-system"
                    :test? true}
                   {:name "helpers"
                    :group "helpers"
                    :test? false}
                   {:name "helpers-test"
                    :group "helpers"
                    :test? true}])

(deftest anv->alias--a-set-of-environments-without-env-mapping--returns-name-to-alias-map
  (is (= {"backend-system" "bs1",
          "backend-system-test" "bs1-test",
          "banking-system" "bs2",
          "banking-system-test" "bs2-test",
          "helpers" "h",
          "helpers-test" "h-test"}
         (alias/env->alias nil environments))))

(deftest env->alias--a-set-of-environments-with-incomplete-env-mapping--returns-mapped-names-and-undefined-mappings
  (is (= {"helpers" "h", "banking-system" "?1", "backend-system" "?2"}
         (alias/env->alias {:env-short-names {"helpers" "h"}} environments))))
