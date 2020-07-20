(ns polylith.clj.core.create.workspace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interfc :as helper]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-workspace--creates-empty-directories-and-a-deps-edn-config-file
  (let [ws-name "ws1"
        _ (helper/execute-command "" "create" "w" ws-name "se.example")]
    (is (= #{"bases"
             "components"
             "environments"
             "deps.edn"}
           (helper/paths ws-name)))

    (is (= [{:polylith {:vcs "git"
                        :top-namespace "se.example"
                        :color-mode "dark"
                        :env-short-names {}}}]
           (helper/content ws-name "deps.edn")))))
