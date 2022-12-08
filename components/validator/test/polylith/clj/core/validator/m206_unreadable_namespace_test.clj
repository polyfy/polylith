(ns polylith.clj.core.validator.m206-unreadable-namespace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.m206-unreadable-namespace :as m206]))

(def bricks [{:type "base"
              :name "poly-cli"
              :namespaces {:src [{:name "core"
                                  :namespace ""
                                  :invalid true
                                  :file-path "/Users/joakimtengstrand/source/polylith/bases/poly-cli/src/polylith/clj/core/poly_cli/core.clj"
                                  :imports ["polylith.clj.core.command.interface"]}
                                 {:name "api"
                                  :namespace "polylith.clj.core.poly-cli.api"
                                  :file-path "/Users/joakimtengstrand/source/polylith/bases/poly-cli/src/polylith/clj/core/poly_cli/api.clj"
                                  :imports ["clojure.string"]}]
                           :test [{:name "api-argument-mapping-test"
                                   :namespace ""
                                   :invalid true
                                   :file-path "/Users/joakimtengstrand/source/polylith/bases/poly-cli/test/polylith/clj/core/poly_cli/api_argument_mapping_test.clj"
                                   :imports ["clojure.test" "polylith.clj.core.poly-cli.api"]}]}}])

(deftest warning--when-having-unreadable-namespaces--returns-warnings
  (is (= [{:type "warning",
           :code 206,
           :message "Unreadable namespace in poly-cli: /Users/joakimtengstrand/source/polylith/bases/poly-cli/src/polylith/clj/core/poly_cli/core.clj",
           :colorized-message "Unreadable namespace in poly-cli: /Users/joakimtengstrand/source/polylith/bases/poly-cli/src/polylith/clj/core/poly_cli/core.clj"}
          {:type "warning",
           :code 206,
           :message "Unreadable namespace in poly-cli: /Users/joakimtengstrand/source/polylith/bases/poly-cli/test/polylith/clj/core/poly_cli/api_argument_mapping_test.clj",
           :colorized-message "Unreadable namespace in poly-cli: /Users/joakimtengstrand/source/polylith/bases/poly-cli/test/polylith/clj/core/poly_cli/api_argument_mapping_test.clj"}]
         (m206/warnings bricks nil nil "none"))))
