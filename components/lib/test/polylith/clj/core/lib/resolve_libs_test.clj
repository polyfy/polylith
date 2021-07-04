(ns polylith.clj.core.lib.resolve-libs-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.lib.resolve-libs :as r]))

(def src-deps [["com.h2database/h2" {:version "1.4.200", :type "maven", :size 2303679}]
               ["org.clojure/test.check" {:version "1.1.0", :type "maven", :size 39487}]])

(deftest override-lib
  (is (= {"com.h2database/h2" {:size 2303679, :type "maven", :version "1.4.201"}
          "org.clojure/test.check" {:size 39487, :type "maven", :version "1.1.0"}}
         (r/resolve-libs src-deps
                         '{com.h2database/h2 {:mvn/version "1.4.201"}}))))

(deftest keep-lib
  (is (= {"com.h2database/h2" {:size 2303679, :type "maven", :version "1.4.200"}
          "org.clojure/test.check" {:size 39487, :type "maven", :version "1.1.0"}}
         (r/resolve-libs src-deps
                         '{com.h2database/h2 {:mvn/version "1.4.199"}}))))
