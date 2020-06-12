(ns polylith.git.git-test
  (:require [clojure.test :refer :all]
            [polylith.git.interface :as git]))

(deftest diff--when-comparing-two-hashes--return-made-changes
  (is (= ["components/deps/test/polylith/deps/dependencies_test.clj"
          "components/validate/test/polylith/validate/illegal_namespace_deps_test.clj"
          "components/validate/test/polylith/validate/illegal_parameters_test.clj"
          "components/validate/test/polylith/validate/missing_functions_and_macros_test.clj"
          "components/workspace-clj/src/polylith/workspace_clj/componentsfromdisk.clj"
          "components/workspace-clj/src/polylith/workspace_clj/importsfromdisk.clj"
          "todo.txt"]
         (git/diff "."
                   "675293c20084a24ca0ed7865cd8bc9114313158f"
                   "2d4640a9f7180ac0286f1106792d2a810318faa0"))))
