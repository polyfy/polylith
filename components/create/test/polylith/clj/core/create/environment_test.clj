(ns polylith.clj.core.create.environment-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interfc :as helper]
            [polylith.clj.core.util.interfc.color :as color]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-environment--performs-expected-actions
  (let [ws-name "ws1"
        dir (str ws-name "/environments/env1")
        output (with-out-str
                 (helper/execute-command "" "create" "w" ws-name "se.example")
                 (helper/execute-command ws-name "create" "e" "env1" "env"))]

    (is (= "You are recommended to manually add an alias to the :env-short-names key in 'deps.edn', e.g.: {\"env1\" \"e\"}\n"
           (color/clean-colors output)))

    (is (= #{"components"
             "bases"
             "environments"
             "environments/env1"
             "environments/env1/deps.edn"
             "deps.edn"}
           (helper/paths ws-name)))

    (is (= '({:aliases {:test {:extra-deps  {}
                               :extra-paths []}}
              :deps {org.clojure/clojure {:mvn/version "1.10.1"}
                     org.clojure/tools.deps.alpha {:mvn/version "0.8.695"}}
              :paths []})
           (helper/content dir "deps.edn")))))
