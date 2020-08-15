(ns polylith.clj.core.entity.path-selector-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.entity.path-selector :as selector]
            [polylith.clj.core.entity.test-data :as test-data]))

(deftest all-src-paths--execute--returns-all-src-paths
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
          "components/user/resources"
          "components/user/src"
          "components/user/src"
          "development/src"]
         (selector/all-src-paths test-data/path-infos))))

(deftest all-test-paths--execute--returns-all-test-paths
  (is (= ["bases/cli/test"
          "components/address/test"
          "components/database/test"
          "components/invoicer/test"
          "components/purchaser/test"
          "components/user/test"
          "components/user/test"
          "development/test"
          "environments/invoice/test"]
         (selector/all-test-paths test-data/path-infos))))

(deftest brick->src-paths--execute--returns-brick->path-map
  (is (= {"address"   #{"components/address/src"
                        "components/address/resources"},
          "cli"       #{"bases/cli/src"
                        "bases/cli/resources"},
          "database"  #{"components/database/src"
                        "components/database/resources"}
          "invoicer"  #{"components/invoicer/src"
                        "components/invoicer/resources"},
          "purchaser" #{"components/purchaser/resources"
                        "components/purchaser/src"},
          "user"      #{"components/user/resources"
                        "components/user/src"},}
         (selector/brick->src-paths test-data/path-infos))))

(deftest brick->test-paths--execute--returns-brick->path-map
  (is (= {"address"   #{"components/address/test"}
          "cli"       #{"bases/cli/test"}
          "database"  #{"components/database/test"}
          "invoicer"  #{"components/invoicer/test"}
          "purchaser" #{"components/purchaser/test"}
          "user"      #{"components/user/test"}}
         (selector/brick->test-paths test-data/path-infos))))

(deftest env->src-paths--execute--returns-brick->path-map
  (is (= {}
         (selector/env->src-paths test-data/path-infos))))

(deftest env->test-paths--execute--returns-brick->path-map
  (is (= {"invoice" #{"environments/invoice/test"}}
         (selector/env->test-paths test-data/path-infos))))
