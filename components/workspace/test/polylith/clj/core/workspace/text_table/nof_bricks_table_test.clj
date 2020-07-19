(ns polylith.clj.core.workspace.text-table.nof-bricks-table-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [polylith.clj.core.workspace.text-table.nof-bricks-table :as nof-table]
            [polylith.clj.core.util.interfc.color :as color]))

(def interfaces (repeat 14 nil))
(def components (repeat 15 nil))
(def ws-bases (repeat 3 nil))
(def environments (repeat 2 nil))

(deftest table--interfaces-components-bases-and-environments--returns-correct-list
  (is (= ["  interfaces:   14"
          "  components:   15"
          "  bases:         3"
          "  environments:  2"]
         (str/split-lines
           (nof-table/table interfaces components ws-bases environments color/none)))))
