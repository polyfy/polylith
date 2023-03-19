(ns polylith.clj.core.path-finder.select-lib-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.test-data :as test-data]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.select :as select]))

(deftest all-src-deps--select-src-deps--returns-src-deps
  (is (= (select/lib-deps test-data/dep-entries c/src?)
         {"org.clojure/clojure" #:mvn{:version "1.10.1"},
          "org.clojure/tools.deps"#:mvn{:version "0.16.1264"},
          "net.mikera/core.matrix" #:mvn{:version "0.62.0"}})))

(deftest all-src-deps--select-test-deps--returns-test-deps
  (is (= (select/lib-deps test-data/dep-entries c/test?)
         {"zprint" #:mvn{:version "0.4.15"}})))
