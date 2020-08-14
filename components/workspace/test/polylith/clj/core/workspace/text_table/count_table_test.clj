(ns polylith.clj.core.workspace.text-table.count-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.text-table.new-count-table :as count-table]))

(def workspace {:settings {:color-mode "none"
                           :thousand-sep ","}
                :environments (repeat 2 nil)
                :bases (repeat 3 nil)
                :components (repeat 15 nil)
                :interfaces (repeat 14 nil)})

(deftest table--interfaces-components-bases-and-environments--returns-correct-list
  (is (= ["  environments: 2   interfaces: 14"
          "  bases:        3   components: 15"]
         (count-table/table workspace))))
