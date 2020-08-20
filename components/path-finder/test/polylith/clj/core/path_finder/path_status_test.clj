(ns polylith.clj.core.path-finder.path-status-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.path-finder.interfc.path-status :as path-status]
            [polylith.clj.core.path-finder.path-extractor :as path-extractor]))

(def path-entries (with-redefs [file/exists (fn [_] true)]
                    (path-extractor/single-path-entries "."
                                                        ["components/address/src"
                                                         "components/user/src"
                                                         "components/user/resources"]
                                                        false false)))

(deftest status-flags--without-resources-flag--returns-src-test-resources-dir-exists-flags
  (is (= "x-"
         (path-status/brick-status-flags path-entries "user" false))))

(deftest status-flags--with-resources-flag--returns-src-test-resources-dir-exists-flags
  (is (= "xx-"
         (path-status/brick-status-flags path-entries "user" true))))
