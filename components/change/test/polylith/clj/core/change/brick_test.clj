(ns polylith.clj.core.change.brick-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.entity :as brick]))

(def files ["bases/core/src/polylith/core/main.clj"
            "bases/core/test/polylith/core/main_test.clj"
            "bases/core/test/polylith/workspace/main_test.clj"
            "components/cmd/src/polylith/cmd/compile.clj"
            "components/cmd/src/polylith/cmd/test.clj"
            "components/workspace/src/polylith/core/core.clj"
            "components/workspace/src/polylith/core/interface.clj"
            "components/workspace/src/polylith/core/interfaces.clj"
            "deps.edn"
            "todo.txt"])

(deftest bricks--when-having-a-list-of-changed-files--return-bases-and-components
  (is (= (brick/changed-entities files {:missing []})
         {:changed-bases ["core"]
          :changed-components ["cmd" "workspace"]
          :changed-projects []})))
