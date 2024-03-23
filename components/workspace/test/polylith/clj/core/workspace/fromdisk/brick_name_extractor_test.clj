(ns polylith.clj.core.workspace.fromdisk.brick-name-extractor-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.fromdisk.brick-name-extractor :as brick-name-extractor]))

(deftest is-dev-component-project
  (is (= false
         (brick-name-extractor/brick-name "../../components/invoicer" true))))

(deftest is-not-dev-component-project
  (is (= "invoicer"
         (brick-name-extractor/brick-name "../../components/invoicer" false))))

(deftest is-dev-component-project
  (is (= nil
         (brick-name-extractor/brick-name "../../bases/mybase" true))))

(deftest is-not-dev-component-project
  (is (= "mybase"
         (brick-name-extractor/brick-name "../../bases/mybase" false))))

(deftest is-dev-component
  (is (= "invoicer"
         (brick-name-extractor/brick-name "components/invoicer" true))))

(deftest is-not-dev-component
  (is (= nil
         (brick-name-extractor/brick-name "components/invoicer" false))))

(deftest is-dev-component
  (is (= "mybase"
         (brick-name-extractor/brick-name "bases/mybase" true))))

(deftest is-not-dev-component
  (is (= nil
         (brick-name-extractor/brick-name "bases/mybase" false))))

(deftest is-dev-component-local
  (is (= "invoicer"
         (brick-name-extractor/brick-name "./components/invoicer" true))))

(deftest is-not-dev-component-local
  (is (= nil
         (brick-name-extractor/brick-name "./components/invoicer" false))))

(deftest is-dev-component-local
  (is (= "mybase"
         (brick-name-extractor/brick-name "./bases/mybase" true))))

(deftest is-not-dev-component-local
  (is (= nil
         (brick-name-extractor/brick-name "./bases/mybase" false))))
