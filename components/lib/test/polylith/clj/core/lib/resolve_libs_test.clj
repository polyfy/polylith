(ns polylith.clj.core.lib.resolve-libs-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.lib.resolve-libs :as r]))

(def src-deps [["com.h2database/h2" {:version "1.4.200", :type "maven", :size 200}]
               ["com.h2database/h2" {:version "1.4.202", :type "maven", :size 202}]
               ["org.clojure/test.check" {:version "1.1.0", :type "maven", :size 111}]])

(deftest resolve-lib
  (is (= (r/resolve-libs src-deps
                         {"com.h2database/h2" {:version "1.4.201", :type "maven", :size 201}})
         {"com.h2database/h2" {:size 201, :type "maven", :version "1.4.201"}
          "org.clojure/test.check" {:size 111, :type "maven", :version "1.1.0"}})))
