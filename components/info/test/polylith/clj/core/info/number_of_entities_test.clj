(ns polylith.clj.core.info.number-of-entities-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.info.table.number-of-entities :as number-of-entities]))

(def workspace {:settings {:color-mode "none"
                           :thousand-separator ","}
                :projects (repeat 2 nil)
                :bases (repeat 3 nil)
                :components (repeat 15 nil)
                :interfaces (repeat 14 nil)})

(deftest table--interfaces-components-bases-and-projects--returns-correct-list
  (is (= ["  projects: 2   interfaces: 14"
          "  bases:    3   components: 15"]
         (number-of-entities/table workspace))))
