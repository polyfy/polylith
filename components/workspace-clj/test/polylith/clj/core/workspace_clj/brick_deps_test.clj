(ns polylith.clj.core.workspace-clj.brick-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace-clj.brick-deps :as brick-deps]))

(deftest is-dev-component-project
  (is (= false
         (brick-deps/brick-name "../../components/invoicer" true))))

(deftest is-not-dev-component-project
  (is (= "invoicer"
         (brick-deps/brick-name "../../components/invoicer" false))))

(deftest is-dev-component-project
  (is (= nil
         (brick-deps/brick-name "../../bases/mybase" true))))

(deftest is-not-dev-component-project
  (is (= "mybase"
         (brick-deps/brick-name "../../bases/mybase" false))))


(deftest is-dev-component
  (is (= "invoicer"
         (brick-deps/brick-name "components/invoicer" true))))

(deftest is-not-dev-component
  (is (= nil
         (brick-deps/brick-name "components/invoicer" false))))

(deftest is-dev-component
  (is (= "mybase"
         (brick-deps/brick-name "bases/mybase" true))))

(deftest is-not-dev-component
  (is (= nil
         (brick-deps/brick-name "bases/mybase" false))))

(deftest is-dev-component-local
  (is (= "invoicer"
         (brick-deps/brick-name "./components/invoicer" true))))

(deftest is-not-dev-component-local
  (is (= nil
         (brick-deps/brick-name "./components/invoicer" false))))

(deftest is-dev-component-local
  (is (= "mybase"
         (brick-deps/brick-name "./bases/mybase" true))))

(deftest is-not-dev-component-local
  (is (= nil
         (brick-deps/brick-name "./bases/mybase" false))))
