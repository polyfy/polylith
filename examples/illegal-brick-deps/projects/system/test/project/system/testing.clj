(ns project.system.testing
  (:require [clojure.test :refer :all]
            [direct.ref.mybase.core :as mybase]))

(deftest illegal-direct-ref
  (is (= "hi"
         (mybase/say-hi))))
