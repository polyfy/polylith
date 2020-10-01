(ns polylith.clj.core.path-finder.splitter-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.profile-src-splitter :as splitter]))

(def settings {:active-profiles #{"default" "admin"}
               :profile-to-settings {"default" {:lib-deps {"org.clojure/clojure" {:mvn/version "1.10.1"}}
                                                :paths ["components/user/src"
                                                        "components/user/resources"
                                                        "components/user/test"
                                                        "environments/invoice/test"]}
                                     "admin" {:lib-deps {"org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}}
                                              :paths ["components/admin/src"
                                                      "components/admin/resources"
                                                      "components/admin/test"]}}})

(deftest extract-paths--from-non-dev-environment--returns-no-profile-paths
  (is (= {:profile-src-paths []
          :profile-test-paths []}
         (splitter/extract-active-profiles-paths false settings))))

(deftest extract-paths--from-dev-environment--returns-src-and-test-paths-from-selected-profiles
  (is (= {:profile-src-paths  ["components/admin/src"
                               "components/admin/resources"
                               "components/user/src"
                               "components/user/resources"]
          :profile-test-paths ["components/admin/test"
                               "components/user/test"
                               "environments/invoice/test"]}
         (splitter/extract-active-profiles-paths true settings))))
