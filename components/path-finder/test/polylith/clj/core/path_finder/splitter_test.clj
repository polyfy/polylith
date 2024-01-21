(ns polylith.clj.core.path-finder.splitter-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.profile-src-splitter :as splitter]))

(def settings {:active-profiles #{"default" "admin"}})

(def profiles [{:name "admin"
                :type "profile"
                :lib-deps {"org.clojure/tools.deps"{:mvn/version "0.16.1264"}}
                :paths ["components/admin/src"
                        "components/admin/resources"
                        "components/admin/test"]}
               {:name "default"
                :type "profile"
                :lib-deps {"org.clojure/clojure" {:mvn/version "1.10.1"}}
                :paths ["components/user/src"
                        "components/user/resources"
                        "components/user/test"
                        "projects/invoice/test"]}])

(deftest extract-paths--from-non-dev-project--returns-no-profile-paths
  (is (= {:profile-src-paths []
          :profile-test-paths []}
         (splitter/extract-active-profiles-paths false profiles settings))))

(deftest extract-paths--from-dev-project--returns-src-and-test-paths-from-selected-profiles
  (is (= {:profile-src-paths  ["components/admin/src"
                               "components/admin/resources"
                               "components/user/src"
                               "components/user/resources"]
          :profile-test-paths ["components/admin/test"
                               "components/user/test"
                               "projects/invoice/test"]}
         (splitter/extract-active-profiles-paths true profiles settings))))
