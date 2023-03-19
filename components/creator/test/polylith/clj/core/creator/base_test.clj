(ns polylith.clj.core.creator.base-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.creator.brick :as brick]
            [polylith.clj.core.test-helper.interface :as helper]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-base--when-component-already-exists--return-error-message
  (let [output (with-out-str
                 (helper/execute-command "" "create" "w" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "b" "name:my-base")
                 (helper/execute-command "ws1" "create" "base" "name:my-base"))]
    (is (= output
           (str brick/create-brick-message "\n"
                "  The brick 'my-base' already exists.\n")))))

(deftest create-base--performs-expected-actions
  (let [src-api-dir "ws1/bases/my-base/src/se/example/my_base"
        test-api-dir "ws1/bases/my-base/test/se/example/my_base"
        output (with-out-str
                 (helper/execute-command "" "create" "w" "name:ws1" "top-ns:se.example" ":commit")
                 (helper/execute-command "ws1" "create" "b" "name:my-base"))]
    (is (= output
           (str brick/create-brick-message "\n")))

    (is (= (helper/paths "ws1")
           #{".git"
             ".gitignore"
             ".vscode"
             ".vscode/settings.json"
             "bases"
             "bases/.keep"
             "bases/my-base"
             "bases/my-base/deps.edn"
             "bases/my-base/resources"
             "bases/my-base/resources/my-base"
             "bases/my-base/resources/my-base/.keep"
             "bases/my-base/src"
             "bases/my-base/src/se"
             "bases/my-base/src/se/example"
             "bases/my-base/src/se/example/my_base"
             "bases/my-base/src/se/example/my_base/core.clj"
             "bases/my-base/test"
             "bases/my-base/test/se"
             "bases/my-base/test/se/example"
             "bases/my-base/test/se/example/my_base"
             "bases/my-base/test/se/example/my_base/core_test.clj"
             "components"
             "components/.keep"
             "deps.edn"
             "development"
             "development/src"
             "development/src/.keep"
             "logo.png"
             "projects"
             "projects/.keep"
             "readme.md"
             "workspace.edn"}))

    (is (= (helper/content src-api-dir "core.clj")
           ["(ns se.example.my-base.core)"]))

    (is (= (helper/content test-api-dir "core_test.clj")
           ["(ns se.example.my-base.core-test"
            "  (:require [clojure.test :as test :refer :all]"
            "            [se.example.my-base.core :as core]))"
            ""
            "(deftest dummy-test"
            "  (is (= 1 1)))"]))))
