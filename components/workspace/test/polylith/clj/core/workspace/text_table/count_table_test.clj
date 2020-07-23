(ns polylith.clj.core.workspace.text-table.count-table-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [polylith.clj.core.workspace.text-table.count-table :as count-table]
            [polylith.clj.core.util.interfc.color :as color]))

(def interfaces (repeat 14 nil))
(def components (repeat 15 nil))
(def ws-bases (repeat 3 nil))
(def environments (repeat 2 nil))

(deftest table--interfaces-components-bases-and-environments--returns-correct-list
  (is (= ["  environments: 2   interfaces: 14"
          "  bases:        3   components: 15"]
         (str/split-lines
           (count-table/table interfaces components ws-bases environments color/none)))))
