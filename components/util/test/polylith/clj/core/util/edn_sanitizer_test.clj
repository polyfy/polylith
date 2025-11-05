(ns polylith.clj.core.util.edn-sanitizer-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.edn-sanitizer :as sut]))

(deftest sanitize-keywords
  (= {:abc 123
      "@abc" 123})
      "@nnn/@abc" 123
     (sut/sanitize-keywords {:abc 123
                             (keyword "@abc") 123
                             (keyword "@nnn" "@abc") 123}))
