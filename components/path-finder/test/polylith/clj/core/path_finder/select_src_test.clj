(ns polylith.clj.core.path-finder.select-src-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.test-data :as test-data]))

(deftest all-src-paths--when-executed--returns-src-paths-from-all-components-bases-and-entities-including-profile-paths
  (is (= ["bases/cli/resources"
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
         (select/paths test-data/path-entries c/src?))))

(deftest all-test-paths--when-executed--returns-test-paths-from-all-components-bases-and-entities-including-profile-paths
  (is (= ["bases/cli/test"
          "components/address/test"
          "components/database/test"
          "components/invoicer/test"
          "components/purchaser/test"
          "components/user/test"
          "development/test"
          "projects/invoice/test"]
         (select/paths test-data/path-entries c/test?))))

(deftest brick-src-entries--when-executed--returns-entries-collected-from-component-and-base-src-paths
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
           :path       "components/user/resources"
           :profile?   true
           :source-dir "resources"
           :test?      false
           :type       :component}
          {:exists?    true
           :name       "user"
           :path       "components/user/src"
           :profile?   true
           :source-dir "src"
           :test?      false
           :type       :component}]
         (select/entries test-data/path-entries c/src? (c/=name "user")))))

(deftest src-component-names--when-executed--returns-expected-result
  (is (= ["address"
          "database"
          "invoicer"
          "purchaser"
          "user"]
         (select/names test-data/path-entries c/component?))))
