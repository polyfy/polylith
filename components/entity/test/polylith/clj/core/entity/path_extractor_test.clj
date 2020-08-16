(ns polylith.clj.core.entity.path-extractor-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.entity.test-data :as test-data]
            [polylith.clj.core.entity.path-extractor :as extractor]
            [polylith.clj.core.file.interfc :as file]))

(deftest path-entries--lists-of-paths--returns-extracted-path-entries
  (with-redefs [file/exists (fn [_] true)]
    (is (= test-data/path-entries
           (extractor/path-entries
             "."
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
             ["components/user/resources" "components/user/src"]
             ["components/user/test"])))))
