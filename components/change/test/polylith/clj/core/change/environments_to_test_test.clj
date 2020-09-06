(ns polylith.clj.core.change.environments-to-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.change.environments-test-data :as data]
            [polylith.clj.core.change.environments-to-test :as to-test]))

(deftest environments-to-test--with-no-changed-bricks--returns-no-environments
  (with-redefs [file/exists (fn [_] true)]
    (is (= {"cli" []
            "core" []
            "development" []}
           (to-test/env->environments-to-test "." data/environments [] false)))))
