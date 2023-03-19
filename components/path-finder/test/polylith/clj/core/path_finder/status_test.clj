(ns polylith.clj.core.path-finder.status-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.interface.status :as status]
            [polylith.clj.core.path-finder.path-extractor :as path-extractor]))

(def paths ["components/address/src"
            "components/user/src"
            "components/user/resources"])

(def path-entries (path-extractor/single-path-entries nil paths false false))

(deftest status-flags--without-resources-flag--returns-src-test-resources-dir-exists-flags
  (is (= (status/brick-status-flags path-entries "user" false)
         "s-")))

(deftest status-flags--with-resources-flag--returns-src-test-resources-dir-exists-flags
  (is (= (status/brick-status-flags path-entries "user" true)
         "sr-")))
