(ns workspace-test
  (:require [clojure.test :refer :all]
            [polylith.common.workspace :as workspace]))

(deftest filter-declarations--require-is-first-statement--returns-def-statements
  (let [code '((ns polylith.spec.interface
                 (:require [polylith.spec.core :as core]))
               (defn valid-config? ['config]
                 (core/valid-config? 'config)))]
    (is (= '((defn valid-config? ['config]
               (core/valid-config? 'config)))
           (workspace/filter-declarations code)))))

(deftest filter-declarations--require-is-second-statement--returns-def-statements
  (let [code '((ns polylith.spec.interface
                 (:gen-class)
                 (:require [polylith.spec.core :as core]))
               (defn valid-config? ['config]
                 (core/valid-config? 'config)))]
    (is (= '((defn valid-config? ['config]
               (core/valid-config? 'config)))
           (workspace/filter-declarations code)))))
