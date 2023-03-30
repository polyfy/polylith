(ns polylith.clj.core.config-reader.interface-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.config-reader.interface :as config-reader]))

(deftest read-deps-file--substitute-aliases-in-paths
  (is (= (:config (config-reader/read-deps-file "examples/for-test/components/company/deps.edn" "deps.edn"))
         {:paths ["clj" "cljc" "resources"],
          :deps {},
          :aliases {:src-paths ["clj" "cljc"],
                    :resources-path ["resources"],
                    :test-paths ["test" "test2"],
                    :test {:extra-paths ["test" "test2"], :extra-deps {}}}})))
