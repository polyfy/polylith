(ns polylith.core.workspace-clj.definitions-test
  (:require [clojure.test :refer :all]
            [polylith.core.workspace-clj.definitions :as defs]))

(deftest filter-statements--returns-def-statements
  (let [code '((ns polylith.spec.interface
                 (:require [polylith.spec.core :as core]))
               (defn valid-config? ['config]
                 (core/valid-config? 'config)))]
    (is (= '((defn valid-config? ['config]
               (core/valid-config? 'config)))
           (defs/filter-statements code)))))
