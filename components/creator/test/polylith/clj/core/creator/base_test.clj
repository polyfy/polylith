(ns polylith.clj.core.creator.base-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.creator.brick :as brick]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.test-helper.interface :as helper]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-base--when-component-already-exists--return-error-message
  (let [output (str-util/normalize-newline
                 (with-out-str
                   (helper/execute-command "" "create" "w" "name:ws1" "top-ns:se.example" "color-mode:none")
                   (helper/execute-command "ws1" "create" "b" "name:my-base" "color-mode:none")
                   (helper/execute-command "ws1" "create" "base" "name:my-base")))]
    (is (= (str "  The short form 'create w' is deprecated and support for it will be dropped. Please use 'create workspace' instead.\n"
                "  The short form 'create b' is deprecated and support for it will be dropped. Please use 'create base' instead.\n"
                brick/create-brick-message "\n"
                "  The brick 'my-base' already exists.\n")
           output))))

(deftest create-base--with-cljs-dialect
  (let [src-api-dir "ws1/bases/my-base/src/se/example/my_base"
        test-api-dir "ws1/bases/my-base/test/se/example/my_base"
        output (str-util/normalize-newline
                 (with-out-str
                   (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example" ":commit")
                   (helper/execute-command "ws1" "create" "base" "name:my-base" "dialect:cljs")))]
    (is (= (str brick/create-brick-message "\n")
           output))

    (is (= #{".git"
             ".gitignore"
             ".vscode"
             ".vscode/settings.json"
             "bases"
             "bases/.keep"
             "bases/my-base"
             "bases/my-base/deps.edn"
             "bases/my-base/package.json"
             "bases/my-base/resources"
             "bases/my-base/resources/my-base"
             "bases/my-base/resources/my-base/.keep"
             "bases/my-base/src"
             "bases/my-base/src/se"
             "bases/my-base/src/se/example"
             "bases/my-base/src/se/example/my_base"
             "bases/my-base/src/se/example/my_base/core.cljs"
             "bases/my-base/test"
             "bases/my-base/test/se"
             "bases/my-base/test/se/example"
             "bases/my-base/test/se/example/my_base"
             "bases/my-base/test/se/example/my_base/core_test.cljs"
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
             "workspace.edn"}
           (helper/paths "ws1")))

    (is (= ["(ns se.example.my-base.core)"]
           (helper/content src-api-dir "core.cljs")))

    (is (= ["(ns se.example.my-base.core-test"
            "  (:require [clojure.test :as test :refer :all]"
            "            [se.example.my-base.core :as core]))"
            ""
            "(deftest dummy-test"
            "  (is (= 1 1)))"]
           (helper/content test-api-dir "core_test.cljs")))))
