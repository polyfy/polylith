(ns polylith.clj.core.create.environment-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interfc :as helper]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-environment--creates-
  (let [ws-name "ws1"
        dir (str ws-name "/environments/env1")
        _ (helper/execute-command "" "create" "w" ws-name "se.example")
        _ (helper/execute-command ws-name "create" "e" "env1" "env")]
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
