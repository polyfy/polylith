(ns polylith.clj.core.validator.m206-unreadable-namespace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.m206-missing-or-unreadable-namespace :as m206]))

(def bases [{:type "base"
             :name "poly-cli"
             :namespaces {:src [{:name "core"
                                 :namespace ""
                                 :is-invalid true
                                 :file-path "bases/poly-cli/src/polylith/clj/core/poly_cli/core.clj"
                                 :imports ["polylith.clj.core.command.interface"]}
                                {:name "extra-util"
                                 :namespace ""
                                 :file-path "bases/poly-cli/src/polylith/clj/core/poly_cli/extra.clj"
                                 :imports []}
                                {:name "api"
                                 :namespace "polylith.clj.core.poly-cli.api"
                                 :is-ignored true
                                 :file-path "bases/poly-cli/src/polylith/clj/core/poly_cli/api.clj"
                                 :imports ["clojure.string"]}]
                          :test [{:name "api-argument-mapping-test"
                                  :namespace ""
                                  :is-invalid true
                                  :file-path "bases/poly-cli/test/polylith/clj/core/poly_cli/api_argument_mapping_test.clj"
                                  :imports ["clojure.test" "polylith.clj.core.poly-cli.api"]}]}}])

(deftest warning--when-having-unreadable-namespaces--returns-warnings
  (is (= (m206/warnings nil bases nil "none")
         [{:code              206
           :colorized-message "Missing or unreadable namespace in poly-cli: bases/poly-cli/src/polylith/clj/core/poly_cli/core.clj"
           :message           "Missing or unreadable namespace in poly-cli: bases/poly-cli/src/polylith/clj/core/poly_cli/core.clj"
           :type              "warning"}
          {:code              206
           :colorized-message "Missing or unreadable namespace in poly-cli: bases/poly-cli/test/polylith/clj/core/poly_cli/api_argument_mapping_test.clj"
           :message           "Missing or unreadable namespace in poly-cli: bases/poly-cli/test/polylith/clj/core/poly_cli/api_argument_mapping_test.clj"
           :type              "warning"}])))
