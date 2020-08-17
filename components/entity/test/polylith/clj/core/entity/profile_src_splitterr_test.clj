(ns polylith.clj.core.entity.profile-src-splitterr-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.entity.profile-src-splitter :as extractor]))

(def settings {:active-dev-profiles #{:default :admin},
               :profile->settings {:default {:lib-deps {"org.clojure/clojure" {:mvn/version "1.10.1"}}
                                             :paths ["components/user/src"
                                                     "components/user/resources"
                                                     "components/user/test"
                                                     "environments/invoice/test"]}
                                   :admin {:lib-deps {"org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}}
                                           :paths ["components/admin/src"
                                                   "components/admin/resources"
                                                   "components/admin/test"]}}})

(deftest extract-paths--from-non-dev-environment--returns-no-profile-paths
  (is (= {:profile-src-paths []
          :profile-test-paths []}
         (extractor/extract-paths false settings))))

(deftest extract-paths--from-dev-environment--returns-src-and-test-paths-from-active-profiles
  (is (= {:profile-src-paths  ["components/admin/src"
                               "components/admin/resources"
                               "components/user/src"
                               "components/user/resources"]
          :profile-test-paths ["components/admin/test"
                               "components/user/test"
                               "environments/invoice/test"]}
         (extractor/extract-paths true settings))))

(deftest extract-deps--from-non-dev-environment--returns-no-profile-deps
  (is (= {}
         (extractor/extract-deps false settings))))

(deftest extract-deps--from-dev-environment--returns-deps-from-active-profiles
  (is (= {"org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"},
          "org.clojure/clojure" {:mvn/version "1.10.1"}}
         (extractor/extract-deps true settings))))
