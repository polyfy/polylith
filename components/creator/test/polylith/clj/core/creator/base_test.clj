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
    (is (= (str brick/create-brick-message "\n"
                "The brick 'my-base' already exists.\n")
           output))))

(deftest create-base--performs-expected-actions
  (let [src-api-dir "ws1/bases/my-base/src/se/example/my_base"
        test-api-dir "ws1/bases/my-base/test/se/example/my_base"
        output (with-out-str
                 (helper/execute-command "" "create" "w" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "b" "name:my-base"))]
    (is (= (str brick/create-brick-message "\n")
           output))

    (is (= #{"bases"
             "bases/my-base"
             "bases/my-base/resources"
             "bases/my-base/resources/my-base"
             "bases/my-base/resources/my-base/.keep"
             "bases/my-base/src"
             "bases/my-base/src/se"
             "bases/my-base/src/se/example"
             "bases/my-base/src/se/example/my_base"
             "bases/my-base/src/se/example/my_base/api.clj"
             "bases/my-base/test"
             "bases/my-base/test/se"
             "bases/my-base/test/se/example"
             "bases/my-base/test/se/example/my_base"
             "bases/my-base/test/se/example/my_base/api_test.clj"
             "components"
             "development"
             "development/src"
             "development/src/.keep"
             "environments"
             ".git"
             ".gitignore"
             "deps.edn"
             "logo.png"
             "readme.md"}
           (helper/paths "ws1")))

    (is (= ["(ns se.example.my-base.api)"]
           (helper/content src-api-dir "api.clj")))

    (is (= ["(ns se.example.my-base.api-test"
            "  (:require [clojure.test :refer :all]))"]
           (helper/content test-api-dir "api_test.clj")))))
