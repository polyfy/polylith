(ns polylith.clj.core.workspace.fromdisk.brick-name-extractor-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.fromdisk.brick-name-extractor :as brick-name-extractor]))

(deftest not-component-from-dev-project
  (is (= nil
         (brick-name-extractor/brick-name "../../components/invoicer" true))))

(deftest component-from-dev-project
  (is (= "invoicer"
         (brick-name-extractor/brick-name "../../components/invoicer" false))))

(deftest not-base-from-dev-project
  (is (= nil
         (brick-name-extractor/brick-name "../../bases/mybase" true))))

(deftest base-from-project
  (is (= "mybase"
         (brick-name-extractor/brick-name "../../bases/mybase" false))))

(deftest component-from-dev-project
  (is (= "invoicer"
         (brick-name-extractor/brick-name "components/invoicer" true))))

(deftest not-component-from-project
  (is (= nil
         (brick-name-extractor/brick-name "components/invoicer" false))))

(deftest base-from-dev-project
  (is (= "mybase"
         (brick-name-extractor/brick-name "bases/mybase" true))))

(deftest not-base-from-project
  (is (= nil
         (brick-name-extractor/brick-name "bases/mybase" false))))

(deftest component-from-dev-project-local
  (is (= "invoicer"
         (brick-name-extractor/brick-name "./components/invoicer" true))))

(deftest not-component-from-project-local
  (is (= nil
         (brick-name-extractor/brick-name "./components/invoicer" false))))

(deftest base-from-dev-project-local
  (is (= "mybase"
         (brick-name-extractor/brick-name "./bases/mybase" true))))

(deftest not-base-from-project-local
  (is (= nil
         (brick-name-extractor/brick-name "./bases/mybase" false))))
