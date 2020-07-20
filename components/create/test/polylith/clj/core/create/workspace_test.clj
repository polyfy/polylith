(ns polylith.clj.core.create.workspace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interfc :as helper]
            [polylith.clj.core.file.interfc :as file]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest polylith-create--missing-namespace--show-error-message
  (with-redefs [file/current-path (fn [] (helper/root-dir))]
      (let [ws-dir (str (helper/root-dir) "/ws1")
            _ (helper/execute-command "create" "w" "ws1" "se.example")]
        (is (= #{"bases"
                 "components"
                 "deps.edn"
                 "environments"}
               (helper/paths ws-dir)))

        (is (= [{:polylith {:vcs "git"
                            :top-namespace "se.example"
                            :color-mode "dark"
                            :env-short-names {}}}]
               (helper/content ws-dir "deps.edn"))))))
