(ns polylith.clj.core.creator.component-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.creator.brick :as brick]
            [polylith.clj.core.test-helper.interface :as helper]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-component--with-missing-name--return-error-message
  (let [output (with-out-str
                 (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "c" "name:"))]
    (is (= "  A brick name must be given.\n"
           output))))

(deftest create-component--when-component-already-exists--return-error-message
  (let [output (with-out-str
                 (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "c" "name:my-component")
                 (helper/execute-command "ws1" "create" "component" "name:my-component"))]
    (is (= (str brick/create-brick-message "\n"
                "  The brick 'my-component' already exists.\n")
           output))))

(deftest create-component--without-giving-an-interface--performs-expected-actions
  (let [src-ifc-dir "ws1/components/my-component/src/se/example/my_component"
        test-ifc-dir "ws1/components/my-component/test/se/example/my_component"
        output (with-out-str
                 (helper/execute-command "" "create" "w" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "c" "name:my-component"))]
    (is (= (str brick/create-brick-message "\n")
           output))

    (is (= #{".gitignore"
             ".vscode"
             ".vscode/settings.json"
             "bases"
             "bases/.keep"
             "components"
             "components/.keep"
             "components/my-component"
             "components/my-component/deps.edn"
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
             "deps.edn"
             "development"
             "development/src"
             "development/src/.keep"
             "logo.png"
             "projects"
             "projects/.keep"
             "readme.md"
             "workspace.edn"}
           (helper/paths "ws1")))

    (is (= ["(ns se.example.my-component.interface)"]
           (helper/content src-ifc-dir "interface.clj")))

    (is (= ["(ns se.example.my-component.interface-test"
            "  (:require [clojure.test :as test :refer :all]"
            "            [se.example.my-component.interface :as my-component]))"
            ""
            "(deftest dummy-test"
            "  (is (= 1 1)))"]
           (helper/content test-ifc-dir "interface_test.clj")))))

(deftest create-component--without-with-a-different-interface--performs-expected-actions
  (let [src-ifc-dir "ws1/components/my-component/src/se/example/my_interface"
        test-ifc-dir "ws1/components/my-component/test/se/example/my_interface"
        output (with-out-str
                 (helper/execute-command "" "create" "w" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "c" "name:my-component" "interface:my-interface"))]
    (is (= (str brick/create-brick-message "\n")
           output))

    (is (= #{".gitignore"
             ".vscode"
             ".vscode/settings.json"
             "bases"
             "bases/.keep"
             "components"
             "components/.keep"
             "components/my-component"
             "components/my-component/deps.edn"
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
             "deps.edn"
             "development"
             "development/src"
             "development/src/.keep"
             "logo.png"
             "projects"
             "projects/.keep"
             "readme.md"
             "workspace.edn"}
           (helper/paths "ws1")))

    (is (= ["(ns se.example.my-interface.interface)"]
           (helper/content src-ifc-dir "interface.clj")))

    (is (= ["(ns se.example.my-interface.interface-test"
            "  (:require [clojure.test :as test :refer :all]"
            "            [se.example.my-interface.interface :as my-interface]))"
            ""
            "(deftest dummy-test"
            "  (is (= 1 1)))"]
           (helper/content test-ifc-dir "interface_test.clj")))))
