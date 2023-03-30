(ns polylith.clj.core.workspace-clj.brick-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace-clj.brick-deps :as brick-deps]))

(deftest is-dev-component-project
  (is (= (brick-deps/brick-name "../../components/invoicer" true)
         false)))

(deftest is-not-dev-component-project
  (is (= (brick-deps/brick-name "../../components/invoicer" false)
         "invoicer")))

(deftest is-dev-component-project
  (is (= (brick-deps/brick-name "../../bases/mybase" true)
         nil)))

(deftest is-not-dev-component-project
  (is (= (brick-deps/brick-name "../../bases/mybase" false)
         "mybase")))


(deftest is-dev-component
  (is (= (brick-deps/brick-name "components/invoicer" true)
         "invoicer")))

(deftest is-not-dev-component
  (is (= (brick-deps/brick-name "components/invoicer" false)
         nil)))

(deftest is-dev-component
  (is (= (brick-deps/brick-name "bases/mybase" true)
         "mybase")))

(deftest is-not-dev-component
  (is (= (brick-deps/brick-name "bases/mybase" false)
         nil)))

(deftest is-dev-component-local
  (is (= (brick-deps/brick-name "./components/invoicer" true)
         "invoicer")))

(deftest is-not-dev-component-local
  (is (= (brick-deps/brick-name "./components/invoicer" false)
         nil)))

(deftest is-dev-component-local
  (is (= (brick-deps/brick-name "./bases/mybase" true)
         "mybase")))

(deftest is-not-dev-component-local
  (is (= (brick-deps/brick-name "./bases/mybase" false)
         nil)))
