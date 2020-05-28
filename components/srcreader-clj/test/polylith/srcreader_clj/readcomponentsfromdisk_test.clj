(ns polylith.srcreader-clj.readcomponentsfromdisk-test
  (:require [clojure.test :refer :all]
            [polylith.srcreader-clj.readcomponentsfrom-disk :as fromdisk]))

(deftest filter-declarations--returns-def-statements
  (let [code '((ns polylith.spec.interface
                 (:require [polylith.spec.core :as core]))
               (defn valid-config? ['config]
                 (core/valid-config? 'config)))]
    (is (= '((defn valid-config? ['config]
               (core/valid-config? 'config)))
           (fromdisk/filter-declarations code)))))
