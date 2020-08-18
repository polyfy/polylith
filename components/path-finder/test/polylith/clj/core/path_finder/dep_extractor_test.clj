(ns polylith.clj.core.path-finder.dep-extractor-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.test-data :as test-data]
            [polylith.clj.core.path-finder.dep-extractor :as dep-extractor]))

(def src-deps {"org.clojure/clojure" {:mvn/version "1.10.1"}
               "org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}})

(def test-deps {"zprint" {:mvn/version "0.4.15"}})

(def profile-deps {"net.mikera/core.matrix" {:mvn/version "0.62.0"}})

(def settings {:active-dev-profiles #{"default"}
               :profile->settings {"default" {:lib-deps {"net.mikera/core.matrix" {:mvn/version "0.62.0"}}}
                                   "admin" {:lib-deps {"org.freemarker/freemarker" {:mvn/version "2.3.28"}}}}})

(deftest dep-entries
  (is (= test-data/dep-entries
         (dep-extractor/dep-entries true src-deps test-deps settings))))

(deftest extract-deps--from-non-dev-environment--returns-no-dependencies
  (is (= {}
         (dep-extractor/extract-deps false settings))))

(deftest extract-deps--from-dev-environment--returns-active-profiles-dependencies
  (is (= {"net.mikera/core.matrix" #:mvn{:version "0.62.0"}}
         (dep-extractor/extract-deps true settings))))
