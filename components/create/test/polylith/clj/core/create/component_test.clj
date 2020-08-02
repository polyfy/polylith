(ns polylith.clj.core.create.component-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interfc :as helper]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-component--when-component-already-exists--return-error-message
  (let [ws-name "ws1"
        output (with-out-str
                 (helper/execute-command "" "create-ws" ws-name "se.example")
                 (helper/execute-command ws-name "create-comp" "my-component")
                 (helper/execute-command ws-name "create-comp" "my-component"))]
    (is (= "The brick 'my-component' already exists.\n"
           output))))

(deftest create-component--performs-expected-actions
  (let [ws-name "ws1"
        src-ifc-dir (str ws-name "/components/my-component/src/se/example/my_component")
        test-ifc-dir (str ws-name "/components/my-component/test/se/example/my_component")
        output (with-out-str
                 (helper/execute-command "" "create-ws" ws-name "se.example")
                 (helper/execute-command ws-name "create-comp" "my-component"))]
    (is (= ""
           output))

    (is (= #{"bases"
             "components"
             "components/my-component"
             "components/my-component/resources"
             "components/my-component/resources/my-component"
             "components/my-component/resources/my-component/.keep"
             "components/my-component/src"
             "components/my-component/src/se"
             "components/my-component/src/se/example"
             "components/my-component/src/se/example/my_component"
             "components/my-component/src/se/example/my_component/interface.clj"
             "components/my-component/test"
             "components/my-component/test/se"
             "components/my-component/test/se/example"
             "components/my-component/test/se/example/my_component"
             "components/my-component/test/se/example/my_component/interface.clj"
             "development"
             "development/src"
             "environments"
             ".git"
             "deps.edn"
             "logo.png"
             "readme.md"}
           (helper/paths ws-name)))

    (is (= ["(ns se.example.my-component.interface)"]
           (helper/content src-ifc-dir "interface.clj")))

    (is (= ["(ns se.example.my-component.interface"
            "  (:require [clojure.test :refer :all]))"]
           (helper/content test-ifc-dir "interface.clj")))))
