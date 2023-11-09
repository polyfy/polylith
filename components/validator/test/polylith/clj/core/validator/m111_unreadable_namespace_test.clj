(ns polylith.clj.core.validator.m111-unreadable-namespace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.m111-unreadable-namespace :as m111]))

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
  (is (= (m111/errors nil bases nil "none")
         [{:code              111
           :colorized-message "Unreadable namespace in poly-cli: bases/poly-cli/src/polylith/clj/core/poly_cli/core.clj. To ignore this error, execute 'poly help check' and follow the instructions for error 111."
           :message           "Unreadable namespace in poly-cli: bases/poly-cli/src/polylith/clj/core/poly_cli/core.clj. To ignore this error, execute 'poly help check' and follow the instructions for error 111."
           :type              "error"}
          {:code              111
           :colorized-message "Unreadable namespace in poly-cli: bases/poly-cli/test/polylith/clj/core/poly_cli/api_argument_mapping_test.clj. To ignore this error, execute 'poly help check' and follow the instructions for error 111."
           :message           "Unreadable namespace in poly-cli: bases/poly-cli/test/polylith/clj/core/poly_cli/api_argument_mapping_test.clj. To ignore this error, execute 'poly help check' and follow the instructions for error 111."
           :type              "error"}])))
