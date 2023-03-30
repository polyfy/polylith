(ns polylith.clj.core.validator.m106-multiple-interface-occurrences-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m106-multiple-interface-occurrences :as m106]))

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

(def projects [{:name "core"
                :group "core"
                :type "project"
                :component-names {:src ["change" "shell" "shell2"]}
                :base-names {:src ["tool"]}
                :paths {:src ["bases/tool/src"
                              "components/change/src"
                              "components/shell/src"
                              "components/shell2/src"]}}])

(deftest errors--when-more-than-one-component-implements-the-same-interface-in-an-project--return-error-message
  (is (= (m106/errors components projects color/none)
         [{:type "error"
           :code 106
           :colorized-message "More than one component that implements the shell interface was found in the core project: shell, shell2"
           :message           "More than one component that implements the shell interface was found in the core project: shell, shell2"
           :interface "shell"
           :components ["shell" "shell2"]
           :project "core"}])))
