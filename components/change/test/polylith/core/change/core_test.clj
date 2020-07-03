(ns polylith.core.change.core-test
  (:require [clojure.test :refer :all]
            [polylith.change.brick :as brick]))

(def filenames ["bases/core/src/polylith/core/main.clj"
                "bases/core/test/polylith/core/main_test.clj"
                "bases/core/test/polylith/workspace/main_test.clj"
                "components/cmd/src/polylith/cmd/compile.clj"
                "components/cmd/src/polylith/cmd/test.clj"
                "components/workspace/src/polylith/core/core.clj"
                "components/workspace/src/polylith/core/interface.clj"
                "components/workspace/src/polylith/core/interfaces.clj"
                "deps.edn"
                "todo.txt"])

(deftest bricks--when-having-a-list-of-workspace-filenames--return-bases-and-components
  (is (= {:bases ["core"]
          :components ["cmd" "workspace"]}
         (brick/bricks filenames))))

(deftest changes--changes-made-between-two-commits--will-return-changed-components-and-bases
  (is (= {:bases ["core"]
          :components ["cmd" "workspace"]}
         (brick/changes "1cedf53463829d53525db8c018e794c0d6020f7"
                        "1d5962f09e8809a8bb48c98483f1e6ea94f8011a"))))
