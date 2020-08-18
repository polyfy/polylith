(ns polylith.clj.core.path-finder.path-extractor-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.test-data :as test-data]
            [polylith.clj.core.path-finder.core :as core]
            [polylith.clj.core.file.interfc :as file]))

(def settings {:active-dev-profiles #{"default"}
               :profile->settings {"default" {:paths ["components/user/resources"
                                                      "components/user/src"
                                                      "components/user/test"]}
                                   "admin" {:paths ["components/admin/resources"
                                                    "components/admin/src"
                                                    "components/admin/test"]}}})

(deftest path-entries--lists-of-paths--returns-extracted-path-entries
  (with-redefs [file/exists (fn [_] true)]
    (is (= test-data/path-entries
           (core/path-entries-from-settings
             "." true
             ["bases/cli/resources"
              "bases/cli/src"
              "components/address/resources"
              "components/address/src"
              "components/database/resources"
              "components/database/src"
              "components/invoicer/resources"
              "components/invoicer/src"
              "components/purchaser/resources"
              "components/purchaser/src"
              "components/user/resources"
              "components/user/src"
              "development/src"]
             ["bases/cli/test"
              "components/address/test"
              "components/database/test"
              "components/invoicer/test"
              "components/purchaser/test"
              "components/user/test"
              "environments/invoice/test"
              "development/test"]
             settings)))))

(deftest profile-entries--when-two-profiles-are-extracted--return-paths
  (is (= [{:exists?    false
           :name       "user"
           :path       "components/user/resources"
           :profile?   false
           :source-dir "resources"
           :test?      false
           :type       :component}
          {:exists?    false
           :name       "user"
           :path       "components/user/src"
           :profile?   false
           :source-dir "src"
           :test?      false
           :type       :component}
          {:exists?    false
           :name       "user"
           :path       "components/user/test"
           :profile?   false
           :source-dir "test"
           :test?      true
           :type       :component}]
         (core/profile-path-entries "." settings "default"))))
