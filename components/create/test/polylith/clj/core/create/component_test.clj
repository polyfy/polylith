(ns polylith.clj.core.create.component-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.create.brick :as brick]
            [polylith.clj.core.test-helper.interfc :as helper]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-component--when-component-already-exists--return-error-message
  (let [output (with-out-str
                 (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "component" "name:my-component")
                 (helper/execute-command "ws1" "create" "component" "name:my-component"))]
    (is (= (str brick/create-brick-message "\n"
                "The brick 'my-component' already exists.\n")
           output))))

(deftest create-component--without-giving-an-interface--performs-expected-actions
  (let [src-ifc-dir "ws1/components/my-component/src/se/example/my_component"
        test-ifc-dir "ws1/components/my-component/test/se/example/my_component"
        output (with-out-str
                 (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "component" "name:my-component"))]
    (is (= (str brick/create-brick-message "\n")
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
             "components/my-component/test/se/example/my_component/interface_test.clj"
             "development"
             "development/src"
             "environments"
             ".git"
             "deps.edn"
             "logo.png"
             "readme.md"}
           (helper/paths "ws1")))

    (is (= ["(ns se.example.my-component.interface)"]
           (helper/content src-ifc-dir "interface.clj")))

    (is (= ["(ns se.example.my-component.interface-test"
            "  (:require [clojure.test :refer :all]))"]
           (helper/content test-ifc-dir "interface_test.clj")))))

(deftest create-component--without-with-a-different-interface--performs-expected-actions
  (let [src-ifc-dir "ws1/components/my-component/src/se/example/my_interface"
        test-ifc-dir "ws1/components/my-component/test/se/example/my_interface"
        output (with-out-str
                 (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "component" "name:my-component" "interface:my-interface"))]
    (is (= (str brick/create-brick-message "\n")
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
             "components/my-component/src/se/example/my_interface"
             "components/my-component/src/se/example/my_interface/interface.clj"
             "components/my-component/test"
             "components/my-component/test/se"
             "components/my-component/test/se/example"
             "components/my-component/test/se/example/my_interface"
             "components/my-component/test/se/example/my_interface/interface_test.clj"
             "development"
             "development/src"
             "environments"
             ".git"
             "deps.edn"
             "logo.png"
             "readme.md"}
           (helper/paths "ws1")))

    (is (= ["(ns se.example.my-interface.interface)"]
           (helper/content src-ifc-dir "interface.clj")))

    (is (= ["(ns se.example.my-interface.interface-test"
            "  (:require [clojure.test :refer :all]))"]
           (helper/content test-ifc-dir "interface_test.clj")))))
