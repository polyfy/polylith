(ns polylith.clj.core.common.config.read-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.common.config.read :as config]))

(deftest read-deps-file--substitute-aliases-in-paths
  (is (= {:paths ["clj" "cljc" "resources"],
          :deps {},
          :aliases {:src-paths ["clj" "cljc"],
                    :resources-path ["resources"],
                    :test-paths ["test" "test2"],
                    :test {:extra-paths ["test" "test2"], :extra-deps {}}}}
         (:config (config/read-deps-file "examples/for-test/components/company/deps.edn")))))
