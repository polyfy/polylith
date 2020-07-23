(ns polylith.clj.core.create.workspace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interfc :as helper]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-workspace--creates-empty-directories-and-a-deps-edn-config-file
  (let [ws-name "ws1"
        output (with-out-str
                 (helper/execute-command "" "create" "w" ws-name "se.example"))]
    (is (= ""
           output))

    (is (= #{"bases"
             "components"
             "environments"
             "images"
             "images/logo.png"
             "deps.edn"
             "readme.md"}
           (helper/paths ws-name)))

    (is (= [{:polylith {:vcs "git"
                        :top-namespace "se.example"
                        :interface-ns "interface"
                        :env-short-names {}}}]
           (helper/content ws-name "deps.edn")))))
