(ns polylith.clj.core.git.interface-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.git.interface :as git]))

(deftest diff--when-comparing-two-hashes--return-changes
  (is (= ["components/workspace-clj/src/polylith/clj/core/workspace_clj/non_top_namespace.clj"
          "components/workspace-clj/test/polylith/clj/core/workspace_clj/non_top_namespace_test.clj"
          "development/src/dev/jocke.clj"]
         (git/diff "."
                   "f83f1867cbfe2132fefee1213adabf0a8a8c10d1"
                   "f96d19facf365bcbff551cada504a44b35c10902"))))
