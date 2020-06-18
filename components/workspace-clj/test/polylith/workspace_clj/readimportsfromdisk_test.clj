(ns polylith.workspace-clj.readimportsfromdisk-test
  (:require [clojure.test :refer :all]
            [polylith.workspace-clj.imports-from-disk :as from-disk]))

(deftest filter-imports--require-is-first-statement--returns-def-statements
  (let [code '((ns polylith.spec.interface
                 (:require [clojure.test :as test]
                           [polylith.spec.core :as core]))
               (defn valid-config? ['config]
                 (core/valid-config? 'config)))]
    (is (= ["clojure.test"
            "polylith.spec.core"]
           (from-disk/filter-imports code)))))

(deftest filter-imports--require-is-second-statement--returns-def-statements
  (let [code '((ns polylith.spec.interface
                 (:gen-class)
                 (:require [clojure.test :as test]
                           [polylith.spec.core :as core]))
               (defn valid-config? ['config]
                 (core/valid-config? 'config)))]
    (is (= ["clojure.test"
            "polylith.spec.core"]
           (from-disk/filter-imports code)))))
