(ns polylith.clj.core.create.base-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interfc :as helper]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-base--when-component-already-exists--return-error-message
  (let [ws-name "ws1"
        output (with-out-str
                 (helper/execute-command "" "create-ws" ws-name "se.example")
                 (helper/execute-command ws-name "create-base" "my-base")
                 (helper/execute-command ws-name "create-base" "my-base"))]
    (is (= "The brick 'my-base' already exists.\n"
           output))))

(deftest create-base--performs-expected-actions
  (let [ws-name "ws1"
        src-api-dir (str ws-name "/bases/my-base/src/se/example/my_base")
        test-api-dir (str ws-name "/bases/my-base/test/se/example/my_base")
        output (with-out-str
                 (helper/execute-command "" "create-ws" ws-name "se.example")
                 (helper/execute-command ws-name "create-base" "my-base"))]
    (is (= ""
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
             "bases/my-base/test/se/example/my_base/api.clj"
             "components"
             "development"
             "development/src"
             "environments"
             ".git"
             "deps.edn"
             "logo.png"
             "readme.md"}
           (helper/paths ws-name)))

    (is (= ["(ns se.example.my-base.api)"]
           (helper/content src-api-dir "api.clj")))

    (is (= ["(ns se.example.my-base.api"
            "  (:require [clojure.test :refer :all]))"]
           (helper/content test-api-dir "api.clj")))))
