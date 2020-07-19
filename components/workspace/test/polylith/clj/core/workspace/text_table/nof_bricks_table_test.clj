(ns polylith.clj.core.workspace.text-table.nof-bricks-table-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [polylith.clj.core.workspace.text-table.nof-bricks-table :as nof-table]
            [polylith.clj.core.util.interfc.color :as color]))

(def environments (repeat 2 nil))
(def ws-bases (repeat 3 nil))
(def components (repeat 15 nil))
(def interfaces (repeat 14 nil))

(deftest table--interfaces-components-bases-and-environments--returns-correct-list
  (is (= ["  environments:  2"
          "  bases:         3"
          "  components:   15"
          "  interfaces:   14"]
         (str/split-lines
           (nof-table/table interfaces components ws-bases environments color/none)))))
