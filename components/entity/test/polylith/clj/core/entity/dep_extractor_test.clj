(ns polylith.clj.core.entity.dep-extractor-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.entity.test-data :as test-data]
            [polylith.clj.core.entity.dep-extractor :as dep-extractor]))

(def src-deps {"org.clojure/clojure" {:mvn/version "1.10.1"}
               "org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}})

(def test-deps {"zprint" {:mvn/version "0.4.15"}})

(def profile-deps {"net.mikera/core.matrix" {:mvn/version "0.62.0"}})

(deftest dep-entries
  (is (= test-data/dep-entries
         (dep-extractor/dep-entries src-deps test-deps profile-deps))))
