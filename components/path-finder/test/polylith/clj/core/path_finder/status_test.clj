(ns polylith.clj.core.path-finder.status-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.path-finder.status :as status]
            [polylith.clj.core.path-finder.path-extractor :as path-extractor]))

(def path-entries (with-redefs [file/exists (fn [_] true)]
                    (path-extractor/path-entries "."
                                                 ["components/address/src"
                                                  "components/user/src"
                                                  "components/user/resources"]
                                                 false false)))

(deftest status-flags--without-resources-flag--returns-src-test-resources-dir-exists-flags
  (is (= "x-"
         (status/status-flags path-entries :brick "user" false))))

(deftest status-flags--with-resources-flag--returns-src-test-resources-dir-exists-flags
  (is (= "xx-"
         (status/status-flags path-entries :brick "user" true))))
