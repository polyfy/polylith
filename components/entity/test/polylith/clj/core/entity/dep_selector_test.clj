(ns polylith.clj.core.entity.dep-selector-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.entity.test-data :as test-data]
            [polylith.clj.core.entity.dep-selector :as selector]))

(deftest all-src-deps--select-src-deps--returns-src-deps
  (is (= {"org.clojure/clojure" #:mvn{:version "1.10.1"},
          "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"},
          "net.mikera/core.matrix" #:mvn{:version "0.62.0"}}
         (selector/all-src-deps test-data/dep-entries))))

(deftest all-src-deps--select-test-deps--returns-test-deps
  (is (= {"zprint" #:mvn{:version "0.4.15"}}
         (selector/all-test-deps test-data/dep-entries))))
