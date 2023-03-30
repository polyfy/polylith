(ns polylith.clj.core.lib.version-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.lib.version :as version]))

(deftest version--extract-version-from-path
  (is (= (version/version "/Users/tengstrand/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar")
         "2.7.7")))
