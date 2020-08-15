(ns polylith.clj.core.entity.test-data
  (:require [clojure.test :refer :all]))

(def path-infos {[:brick
                  "address"
                  :src]  [{:exists?    true
                           :name       "address"
                           :path       "components/address/resources"
                           :profile?   false
                           :source-dir "resources"
                           :test?      false
                           :type       :component}
                          {:exists?    true
                           :name       "address"
                           :path       "components/address/src"
                           :profile?   false
                           :source-dir "src"
                           :test?      false
                           :type       :component}]
                 [:brick
                  "address"
                  :test] [{:exists?    true
                           :name       "address"
                           :path       "components/address/test"
                           :profile?   false
                           :source-dir "test"
                           :test?      true
                           :type       :component}]
                 [:brick
                  "cli"
                  :src]  [{:exists?    true
                           :name       "cli"
                           :path       "bases/cli/resources"
                           :profile?   false
                           :source-dir "resources"
                           :test?      false
                           :type       :base}
                          {:exists?    true
                           :name       "cli"
                           :path       "bases/cli/src"
                           :profile?   false
                           :source-dir "src"
                           :test?      false
                           :type       :base}]
                 [:brick
                  "cli"
                  :test] [{:exists?    true
                           :name       "cli"
                           :path       "bases/cli/test"
                           :profile?   false
                           :source-dir "test"
                           :test?      true
                           :type       :base}]
                 [:brick
                  "database"
                  :src]  [{:exists?    true
                           :name       "database"
                           :path       "components/database/resources"
                           :profile?   false
                           :source-dir "resources"
                           :test?      false
                           :type       :component}
                          {:exists?    true
                           :name       "database"
                           :path       "components/database/src"
                           :profile?   false
                           :source-dir "src"
                           :test?      false
                           :type       :component}]
                 [:brick
                  "database"
                  :test] [{:exists?    true
                           :name       "database"
                           :path       "components/database/test"
                           :profile?   false
                           :source-dir "test"
                           :test?      true
                           :type       :component}]
                 [:brick
                  "invoicer"
                  :src]  [{:exists?    true
                           :name       "invoicer"
                           :path       "components/invoicer/resources"
                           :profile?   false
                           :source-dir "resources"
                           :test?      false
                           :type       :component}
                          {:exists?    true
                           :name       "invoicer"
                           :path       "components/invoicer/src"
                           :profile?   false
                           :source-dir "src"
                           :test?      false
                           :type       :component}]
                 [:brick
                  "invoicer"
                  :test] [{:exists?    true
                           :name       "invoicer"
                           :path       "components/invoicer/test"
                           :profile?   false
                           :source-dir "test"
                           :test?      true
                           :type       :component}]
                 [:brick
                  "purchaser"
                  :src]  [{:exists?    true
                           :name       "purchaser"
                           :path       "components/purchaser/resources"
                           :profile?   false
                           :source-dir "resources"
                           :test?      false
                           :type       :component}
                          {:exists?    true
                           :name       "purchaser"
                           :path       "components/purchaser/src"
                           :profile?   false
                           :source-dir "src"
                           :test?      false
                           :type       :component}]
                 [:brick
                  "purchaser"
                  :test] [{:exists?    true
                           :name       "purchaser"
                           :path       "components/purchaser/test"
                           :profile?   false
                           :source-dir "test"
                           :test?      true
                           :type       :component}]
                 [:brick
                  "user"
                  :src]  [{:exists?    true
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
                 [:brick
                  "user"
                  :test] [{:exists?    true
                           :name       "user"
                           :path       "components/user/test"
                           :profile?   false
                           :source-dir "test"
                           :test?      true
                           :type       :component}
                          {:exists?    true
                           :name       "user"
                           :path       "components/user/test"
                           :profile?   true
                           :source-dir "test"
                           :test?      true
                           :type       :component}]
                 [:env
                  "invoice"
                  :test] [{:exists?    true
                           :name       "invoice"
                           :path       "environments/invoice/test"
                           :profile?   false
                           :source-dir "test"
                           :test?      true
                           :type       :environment}]
                 [:other
                  nil
                  :src]  [{:exists?  true
                           :path     "development/src"
                           :profile? false
                           :test?    false}]
                 [:other
                  nil
                  :test] [{:exists?  true
                           :path     "development/test"
                           :profile? false
                           :test?    true}]})
