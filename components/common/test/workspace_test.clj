(ns workspace-test
  (:require [clojure.test :refer :all]
            [polylith.file.interface :as file]
            [polylith.common.workspace :as workspace]))

(deftest filter-declarations--returns-def-statements
  (let [code '((ns polylith.spec.interface
                 (:require [polylith.spec.core :as core]))
               (defn valid-config? ['config]
                 (core/valid-config? 'config)))]
    (is (= '((defn valid-config? ['config]
               (core/valid-config? 'config)))
           (workspace/filter-declarations code)))))

(deftest filter-imports--require-is-first-statement--returns-def-statements
  (let [code '((ns polylith.spec.interface
                 (:require [clojure.test :as test]
                           [polylith.spec.core :as core]))
               (defn valid-config? ['config]
                 (core/valid-config? 'config)))]
    (is (= '[clojure.test
             polylith.spec.core]
           (workspace/filter-imports code)))))

(deftest filter-imports--require-is-second-statement--returns-def-statements
  (let [code '((ns polylith.spec.interface
                 (:gen-class)
                 (:require [clojure.test :as test]
                           [polylith.spec.core :as core]))
               (defn valid-config? ['config]
                 (core/valid-config? 'config)))]
    (is (= '[clojure.test
             polylith.spec.core]
           (workspace/filter-imports code)))))


(def code (file/read-file "./components/common/src/polylith/common/interface.clj"))

