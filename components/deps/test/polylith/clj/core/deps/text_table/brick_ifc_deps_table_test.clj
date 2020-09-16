(ns polylith.clj.core.deps.text-table.brick-ifc-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.brick-ifc-deps-table :as deps-table]))

(def change {:name "change"
             :type "component"
             :interface-deps ["git" "path-finder" "util"]})

(def workspace {:settings {:color-mode "none"}
                :components [{:name "api"
                              :type "component"
                              :interface-deps ["change" "user-input"]}
                             change
                             {:name "command"
                              :type "component"
                              :interface-deps ["change" "help"]}
                             {:name "deployer"
                              :type "component"
                              :interface-deps ["api" "file" "shell"]}]})

(deftest table--a-brick-with-interface-deps--returns-a-list-with-those-deps
  (is (= ["  used by  <  change  >  uses       "
          "  -------                -----------"
          "  api                    git        "
          "  command                path-finder"
          "                         util       "]
         (deps-table/table workspace change))))
