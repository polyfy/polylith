(ns polylith.clj.core.deps.brick-ifc-deps-table-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [polylith.clj.core.deps.brick-ifc-deps-table :as deps-table]
            [polylith.clj.core.util.interfc.color :as color]))

(def brick {:interface-deps ["change" "file" "util" "workspace" "workspace-clj"]})

(deftest table--a-brick-with-interface-deps--returns-a-list-with-those-deps
  (is (= ["  uses         "
          "  -------------"
          "  change       "
          "  file         "
          "  util         "
          "  workspace    "
          "  workspace-clj"]
         (str/split-lines
           (deps-table/table brick color/none)))))


