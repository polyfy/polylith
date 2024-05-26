(ns polylith.clj.core.util.path-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.path :as path]))

(deftest parent-path
  (is (= "../"
         (path/relative-path "a/b/ccc/"
                             "a/b"))))

(deftest parent-sub-paths
  (is (= "../c/d/e/f"
         (path/relative-path "a/b/ccc"
                             "a/b/c/d/e/f"))))

(deftest current-dir
  (is (= ""
         (path/relative-path "a/b/ccc"
                             "a/b/ccc"))))
