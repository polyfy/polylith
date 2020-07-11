(ns polylith.clj.core.validate.m106-multiple-interface-occurrences-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.validate.m106-multiple-interface-occurrences :as m106]))

(def components [{:name "change"
                  :type "component"
                  :interface {:name "change"}}
                 {:name "shell"
                  :type "component"
                  :interface {:name "shell"}}
                 {:name "shell2"
                  :type "component"
                  :interface {:name "shell"}}
                 {:name "file"
                  :type "component"
                  :interface {:name "file"}}
                 {:name "git"
                  :type "component"
                  :interface {:name "git"}}])

(def environments [{:name "core"
                    :group "core"
                    :type "environment"
                    :component-names ["change" "shell" "shell2"]
                    :base-names ["tool"]
                    :paths ["bases/tool/src"
                            "components/change/src"
                            "components/shell/src"
                            "components/shell2/src"]}])

(deftest errors--when-more-than-one-component-implements-the-same-interface-in-an-environment--return-error-message
  (is (= [{:type "error"
           :code 106
           :colorized-message "More than one component that implements the shell interface was found in the core environment: shell, shell2"
           :message           "More than one component that implements the shell interface was found in the core environment: shell, shell2"
           :interface "shell"
           :components ["shell" "shell2"]
           :environment "core"}]
         (m106/errors components environments color/none))))
