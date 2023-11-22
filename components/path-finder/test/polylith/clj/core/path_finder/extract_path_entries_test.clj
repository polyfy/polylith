(ns polylith.clj.core.path-finder.extract-path-entries-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.test-data :as test-data]
            [polylith.clj.core.path-finder.interface.extract :as extract]))

(def settings {:active-profiles #{"default"}
               :profile-to-settings {"default" {:paths ["components/user/resources"
                                                        "components/user/src"
                                                        "components/user/test"]}
                                     "admin" {:paths ["components/admin/resources"
                                                      "components/admin/src"
                                                      "components/admin/test"]}}})

(def paths {:src ["bases/cli/resources"
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
            :test ["bases/cli/test"
                   "components/address/test"
                   "components/database/test"
                   "components/invoicer/test"
                   "components/purchaser/test"
                   "components/user/test"
                   "projects/invoice/test"
                   "development/test"]})

(deftest path-entries--lists-of-paths--returns-extracted-path-entries
  (is (= test-data/path-entries
         (extract/from-unenriched-project
           true
           paths
           {:missing []}
           settings))))

(deftest profile-entries--when-two-profiles-are-extracted--return-paths
  (is (= [{:exists?    true
           :name       "user"
           :path       "components/user/resources"
           :profile?   false
           :source-dir "resources"
           :test?      false
           :type       :component}
          {:exists?    true
           :name       "user"
           :path       "components/user/src"
           :profile?   false
           :source-dir "src"
           :test?      false
           :type       :component}
          {:exists?    true
           :name       "user"
           :path       "components/user/test"
           :profile?   false
           :source-dir "test"
           :test?      true
           :type       :component}]
         (extract/from-profiles-paths [] settings "default"))))
