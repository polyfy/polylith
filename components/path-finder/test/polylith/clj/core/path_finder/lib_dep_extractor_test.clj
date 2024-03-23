(ns polylith.clj.core.path-finder.lib-dep-extractor-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.path-finder.test-data :as test-data]
            [polylith.clj.core.path-finder.lib-dep-extractor :as lib-dep-extractor]))

(def deps {:src {"org.clojure/clojure" {:mvn/version "1.10.1"}
                 "org.clojure/tools.deps"{:mvn/version "0.16.1264"}}
           :test {"zprint" {:mvn/version "0.4.15"}}})

(def profile-deps {"net.mikera/core.matrix" {:mvn/version "0.62.0"}})

(def settings {:active-profiles #{"default"}})

(def profiles [{:name "default"
                :lib-deps {"net.mikera/core.matrix" {:mvn/version "0.62.0"}}}])

(deftest dep-entries
  (is (= test-data/dep-entries
         (lib-dep-extractor/from-library-deps true deps profiles settings))))

(deftest extract-deps--from-non-dev-project--returns-no-dependencies
  (is (= {}
         (lib-dep-extractor/extract-profile-deps false profiles settings))))

(deftest extract-deps--from-dev-project--returns-selected-profiles-dependencies
  (is (= {"net.mikera/core.matrix" #:mvn{:version "0.62.0"}}
         (lib-dep-extractor/extract-profile-deps true profiles settings))))
