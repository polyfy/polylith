(ns polylith.clj.core.validator.datashape.toolsdeps2-test
  (:require
   [clojure.test :refer :all]
   [malli.core :as m]
   [polylith.clj.core.validator.datashape.toolsdeps2 :as sut]))

(defn validate-test-runner-config [v]
  (m/validate sut/test-runner-config-schema v))

(deftest test-runner-config-is-optional
  (is (validate-test-runner-config {})))

(deftest valid-create-test-runner
  (are [candidate]
       (validate-test-runner-config
        {:create-test-runner candidate})
    :default
    'my.runner/create
    ['my.runner/create]
    ['my.runner/create :default]
    ['my.runner/create :default 'my.runner/create :default]))

(deftest invalid-create-test-runner
    (are [candidate]
         (not (validate-test-runner-config
               {:create-test-runner candidate}))
      :not-default
      'unqualified
      123
      "my.runner/create"
      []
      {}
      [:not-default]
      ['unqualified]
      ['unqualified :default]))
