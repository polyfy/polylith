(ns polylith.clj.core.workspace.enrich.project-test-settings-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.enrich.project-test-settings :as pts]))

(deftest enrich-replace-empty-vector-with-include
  (is (= (select-keys
           (pts/enrich ["a"] nil)
           [:include])
         {:include ["a"]})))

(deftest enrich-use-global-test-settings-if-not-specified-in-project
  (is (= (select-keys
           (pts/enrich [] {:test {:include ["a"]
                                  :untouched :data}})
           [:include :untouched])
         {:include []
          :untouched :data})))

(deftest enrich-replace-nil-with-default-test-runner
  (is (= (pts/enrich {:create-test-runner [nil]} nil)
         {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]})))

(deftest enrich-replace-default-with-default-test-runner
  (is (= (pts/enrich {:create-test-runner [:default]} nil)
         {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]})))

(deftest enrich-with-multiple-test-runners
  (is (= (pts/enrich {:create-test-runner ['se.example/create 'se.example/create2]}
                     {:test {:create-test-runner 'ignore.me}})
         {:create-test-runner ['se.example/create 'se.example/create2]})))

(deftest enrich-as-vector-with-default-test-runner
  (is (= (pts/enrich {:create-test-runner 'se.example/create} nil)
         {:create-test-runner ['se.example/create]})))
