(ns polylith.clj.core.path-finder.select-lib-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.test-data :as test-data]
            [polylith.clj.core.path-finder.interfc.match :as m]
            [polylith.clj.core.path-finder.interfc.select :as select]))

(deftest all-src-deps--select-src-deps--returns-src-deps
  (is (= {"org.clojure/clojure" #:mvn{:version "1.10.1"},
          "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"},
          "net.mikera/core.matrix" #:mvn{:version "0.62.0"}}
         (select/lib-deps test-data/dep-entries m/src?))))

(deftest all-src-deps--select-test-deps--returns-test-deps
  (is (= {"zprint" #:mvn{:version "0.4.15"}}
         (select/lib-deps test-data/dep-entries m/test?))))
