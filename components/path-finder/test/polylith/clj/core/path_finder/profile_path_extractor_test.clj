(ns polylith.clj.core.path-finder.profile-path-extractor-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.profile-path-extractor :as extractor]))

(def settings {:profile->settings {"default" {:lib-deps {"zprint" "0.4.15"},
                                              :src-paths ["components/user/resources" "components/user/src"],
                                              :test-paths ["components/user/test"]},
                                   "admin" {:lib-deps {},
                                            :src-paths ["components/admin/resources" "components/admin/src"],
                                            :test-paths ["components/admin/test"]}}})

(deftest profile-entries--when-two-profiles-are-extracted--return-paths
  (is (= [{:profile "default"
           :name "user"
           :type :component
           :test? true
           :source-dir "test"
           :exists? false
           :path "components/user/test"}
          {:profile "admin"
           :name "admin"
           :type :component
           :test? true
           :source-dir "test"
           :exists? false
           :path "components/admin/test"}]
         (extractor/profile-entries "." settings))))
