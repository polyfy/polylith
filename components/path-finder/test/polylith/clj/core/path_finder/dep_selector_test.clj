(ns polylith.clj.core.path-finder.dep-selector-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.test-data :as test-data]
            [polylith.clj.core.path-finder.dep-selector :as dep-selector]))

(deftest all-src-deps--select-src-deps--returns-src-deps
  (is (= {"org.clojure/clojure" #:mvn{:version "1.10.1"},
          "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"},
          "net.mikera/core.matrix" #:mvn{:version "0.62.0"}}
         (dep-selector/all-src-deps test-data/dep-entries))))

(deftest all-src-deps--select-test-deps--returns-test-deps
  (is (= {"zprint" #:mvn{:version "0.4.15"}}
         (dep-selector/all-test-deps test-data/dep-entries))))
